package de.conti.tires.mandi.backend.core.validation;

import de.conti.tires.mandi.backend.core.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;


/**
 * Validator that uses annotations to validate fields of an entity object for null, length and unique restrictions.
 * The fields and the parent fields will be evaluated with {@link PropertyUtils} and {@link FieldUtils}.
 * <p>
 * Supported annotations: {@link Entity}, {@link Table}, {@link Column}, {@link JoinColumn}, {@link NotNull}.
 * <p>
 * Notes:
 * <ul>
 * <li> {@link Entity} annotation needs an name attribute </li>
 * <li> {@link Table} annotation is used for multiple field unique constraints</li>
 * <li> {@link NotNull} overrules {@link Column#nullable()} restriction</li>
 * <li> {@link Column#length()} will only affect fields of {@link String} and collections of those, empty strings will be treated as being {@code null}</li>
 * <li> {@link FieldUtils#getAllFields(Class)} contains parent fields (if any)</li>
 * </ul>
 * <p>
 * Unique constraints will be ignored if all related values are {@code null}.
 * <p>
 * Also note: No errors will be generated
 * for unique group members which refer to entity references / UUIDs if {@code ignoreReferenceUniqueErrors} is set.
 *
 * @param <E> Entity type.
 */
@Log4j2
public class AnnotationBasedValidator<E> implements Validator<E>
{
   private final Class<E> type;
   private final boolean ignoreReferenceUniqueErrors;

   /**
    * Maps names of {@link Field} to {@link FieldRestriction}
    */
   protected final Map<String, FieldRestriction> fieldRestrictions = new HashMap<>();

   /**
    * Maps comma-concatenated names of {@link Field} to {@link UniqueConstraintRestriction} to handle constraints with
    * multiple fields.
    * <p>
    * Note: fieldNames for {@link Table} will be resolved with {@link AnnotationBasedValidator#findFieldByName(List, String)}
    */
   protected final Map<String, UniqueConstraintRestriction> uniqueConstraintRestrictions = new HashMap<>();

   private final EntityManager entityManager;

   /**
    * Creates a new validator instance for the given class.
    *
    * @param type                        class to scan for related annotations
    * @param ignoreReferenceUniqueErrors if {@literal true} unique errors for reference fields will raise no error
    * @param entityManager               interface to access the persistence context
    */
   public AnnotationBasedValidator(@NonNull Class<E> type, boolean ignoreReferenceUniqueErrors,
                                   @NonNull EntityManager entityManager)
   {
      List<Field> allFieldsList = FieldUtils.getAllFieldsList(type);

      this.entityManager = entityManager;

      this.type = type;
      processTableAnnotation(type, allFieldsList);
      processFieldAnnotation(allFieldsList);

      this.ignoreReferenceUniqueErrors = ignoreReferenceUniqueErrors;
   }

   /**
    * Processes {@link Table} annotation, to enrich complex entries of {@link UniqueConstraintRestriction}.
    * It depends on the size of declared {@link UniqueConstraint} annotations in {@link Table}.
    *
    * @param type          {@link Class} of an specific {@link Entity}
    * @param allFieldsList all reflected fields of the given {@link Class} and its parents
    */
   private void processTableAnnotation(@NonNull Class<E> type, List<Field> allFieldsList)
   {
      Optional<Table> table = Optional.ofNullable(type.getDeclaredAnnotation(Table.class));

      // skip when there is no table annotation
      if (table.isPresent())
      {
         // loop over all uniqueConstraint annotations
         for (UniqueConstraint constraint : table.get().uniqueConstraints())
         {
            Map<String, Field> uniqueGroup = new HashMap<>();
            List<String> fieldNames = new LinkedList<>();

            // loop over all columnNames to map to corresponding fields
            for (String columnName : constraint.columnNames())
            {
               Field field = findFieldByName(allFieldsList, columnName).get();
               String fieldName = field.getName();

               uniqueGroup.put(field.getName(), field);
               fieldNames.add(fieldName);
            }

            // collect the complex restriction in separated map and use concatenated fieldNames as key
            uniqueConstraintRestrictions.put(join(fieldNames, ','), new UniqueConstraintRestriction(uniqueGroup));
         }
      }
   }

   /**
    * Processes {@link Column}, {@link NotNull} annotations, to enrich entries of {@link FieldRestriction} and {@link UniqueConstraintRestriction}.
    *
    * @param allFieldsList all reflected fields of the given {@link Class} and its parents
    */
   private void processFieldAnnotation(List<Field> allFieldsList)
   {
      for (Field field : allFieldsList)
      {
         boolean notNull = (field.getAnnotation(NotNull.class) != null);
         Integer length = null;

         Column column = field.getAnnotation(Column.class);
         if (column != null)
         {
            // keeps positive initialization to create an overrule
            notNull |= !column.nullable();

            if (column.unique())
            {
               // a unique will create a simple restriction with only one entry
               Map<String, Field> uniqueGroup = new HashMap<>();

               uniqueGroup.put(field.getName(), field);

               // collect the simple restriction in separated map
               uniqueConstraintRestrictions.put(field.getName(), new UniqueConstraintRestriction(uniqueGroup));
            }
         }

         if (checkLengthType(field) && column != null)
         {
            length = column.length();
         }

         if (notNull || length != null)
         {
            fieldRestrictions.put(field.getName(), new FieldRestriction(notNull, length));
         }
      }
   }

   private boolean checkLengthType(@NonNull Field field)
   {
      if (String.class.isAssignableFrom(field.getType()))
      {
         return true;
      }

      if (Collection.class.isAssignableFrom(field.getType()))
      {
         try
         {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            if (String.class.isAssignableFrom((Class<?>) parameterizedType.getActualTypeArguments()[0]))
            {
               return true;
            }
         }
         catch (IndexOutOfBoundsException | GenericSignatureFormatError | TypeNotPresentException |
               MalformedParameterizedTypeException | ClassCastException e)
         {
            log.trace("could not read generic type", e);
         }
      }

      return false;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void validate(E entity, E previousState, Map<String, Object> data,
         ValidationErrors errors)
   {
      try
      {
         validateFieldRestrictions(entity, errors, fieldRestrictions);
         validateUniqueConstraintRestrictions(entity, errors, uniqueConstraintRestrictions);
      }
      catch (ClassCastException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
      {
         log.error("could not read property", e);
      }
   }

   /**
    * Validates entries of {@link FieldRestriction} on an specific {@link Entity}.
    *
    * @param entity       entity to be evaluated
    * @param errors       validation violations
    * @param restrictions restrictions to be used in the validation
    * @throws IllegalAccessException    could not read property
    * @throws NoSuchMethodException     could not read property
    * @throws InvocationTargetException could not read property
    */
   private void validateFieldRestrictions(E entity, ValidationErrors
         errors, Map<String, FieldRestriction> restrictions)
         throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
   {
      for (Map.Entry<String, FieldRestriction> entry : restrictions.entrySet())
      {
         String field = entry.getKey();
         FieldRestriction restriction = entry.getValue();

         Object value = getProperty(entity, field);
         if (restriction.notNull)
         {
            errors.decideRequiredError(field, value);
         }

         if (value instanceof String)
         {
            String stringValue = (String) value;

            Optional.ofNullable(restriction.maxLength)
                  .ifPresent(length -> errors.decideLengthError(field, stringValue, length));

            if (restriction.notNull && stringValue.isEmpty())
            {
               errors.addFieldError(field, "error.validation.required");
            }
         }
         else if (value instanceof Collection && restriction.maxLength != null)
         {
            ((Collection<String>) value)
                  .stream()
                  .filter(StringUtils::isNotEmpty)
                  .filter(item -> item.length() > restriction.maxLength)
                  .findAny()
                  .ifPresent(item -> errors.addFieldError(field, "error.validation.maxLength", restriction.maxLength));
         }
      }
   }

   /**
    * Validates entries of {@link UniqueConstraintRestriction} on an specific {@link Entity} by using a {@link Query}
    * with an {@link EntityManager}. The resultSet will be compared to find violations.
    * <p>
    * Note: all {@link UniqueConstraintRestriction} will be transformed to one single {@link Query}.
    *
    * @param entity       entity to be evaluated
    * @param errors       validation violations
    * @param restrictions restrictions to be used in the validation
    * @throws IllegalAccessException    could not read property
    * @throws NoSuchMethodException     could not read property
    * @throws InvocationTargetException could not read property
    */
   private void validateUniqueConstraintRestrictions(E entity, ValidationErrors errors,
         Map<String, UniqueConstraintRestriction> restrictions)
         throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
   {

      Map<String, Object> parameters = new HashMap<>(); // flat map of fieldName to fieldValue, for faster lookup
      String tableName = type.getDeclaredAnnotation(Entity.class).name();
      String jpqlTemplate = "select x from %s x where %s";
      String whereClause = "";

      // loop over all restrictions to create the whereClause
      for (UniqueConstraintRestriction restriction : restrictions.values())
      {
         Map<String, Field> uniqueGroup = restriction.getUniqueGroup();
         List<String> expressions = new ArrayList<>(uniqueGroup.size());

         Optional<Field> uniqueGroupViolation = uniqueGroup
               .values()
               .stream()
               .filter(field -> errors.getFields().containsKey(field.getName())).findAny();

         // skip expression when there is already at least one error
         if (!uniqueGroupViolation.isPresent())
         {
            int nullValues = createExpressionsCountingNulls(uniqueGroup.values(), entity, parameters, expressions);

            // skip unique test if all values are null
            if (nullValues == uniqueGroup.size())
            {
               continue;
            }

            // create term by joining all expressions for one uniqueGroup with and-operator
            String term = expressions.size() > 1
                  ? String.format("(%s)", join(expressions, " AND "))
                  : expressions.get(0);

            // set term as whereClause when it is blank otherwise append with or-operator
            whereClause = !isBlank(whereClause)
                  ? join(Arrays.asList(whereClause, term), " OR ")
                  : term;
         }
      }

      // skip when there are no terms in the whereClause
      if (StringUtils.isBlank(whereClause))
      {
         return;
      }
      // create query and bind all parameters with the flatted map
      Query query = entityManager.createQuery(String.format(jpqlTemplate, tableName, whereClause));
      query.setFlushMode(FlushModeType.COMMIT);
      parameters.entrySet()
            .stream()
            .filter(entry -> entry.getValue() != null)
            .forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));

      // find and loop over all violated entities
      List<E> violatingEntities = query.getResultList();
      for (E violatingEntity : violatingEntities)
      {
         generateErrorsForViolatingEntity(entity, violatingEntity, errors, restrictions, parameters);
      }
   }

   private Map<String, Field> buildUniqueGroupWithoutReferences(E
         violatingEntity, Map<String, Field> uniqueGroupMembers)
   {
      // don't send uniqueness violation errors for entities / uuids.
      final Set<String> fieldsReferencingEntities = uniqueGroupMembers
            .keySet().stream()
            .filter(field ->
            {
               try
               {
                  Object fieldValue = getProperty(violatingEntity, field);
                  return (fieldValue instanceof BaseEntity);
               }
               catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
               {
                  // should never happen
                  log.warn("Exception during unique constraint validation", e);
                  return true;
               }
            })
            .collect(Collectors.toSet());
      final Map<String, Field> uniqueGroupMembersWithoutReferenceFields = new HashMap<>(uniqueGroupMembers);
      fieldsReferencingEntities.forEach(uniqueGroupMembersWithoutReferenceFields::remove);
      return uniqueGroupMembersWithoutReferenceFields;
   }

   private void generateErrorsForViolatingEntity(E validatedEntity, E violatingEntity, ValidationErrors errors,
         Map<String, UniqueConstraintRestriction> restrictions, Map<String, Object> parameters)
         throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
   {
      // skip self match
      if (!Objects.equals(getProperty(validatedEntity, "uuid"), getProperty(violatingEntity, "uuid")))
      {
         for (UniqueConstraintRestriction restriction : restrictions.values())
         {
            boolean isValid = false;

            for (Field field : restriction.getUniqueGroup().values())
            {
               String fieldName = field.getName();
               Object fieldValue = getProperty(violatingEntity, fieldName);
               isValid |= !Objects.equals(parameters.get(fieldName), fieldValue);
            }
            if (!isValid)
            {
               Map<String, Field> uniqueGroup = restriction.getUniqueGroup();
               if (ignoreReferenceUniqueErrors)
               {
                  uniqueGroup = buildUniqueGroupWithoutReferences(violatingEntity, uniqueGroup);
               }
               uniqueGroup.forEach((s, field) -> errors.addUniqueError(field.getName()));
            }
         }
      }
   }

   private int createExpressionsCountingNulls(@NonNull Collection<Field> groupMembers, @NonNull E entity,
         @NonNull Map<String, Object> parameters, @NonNull List<String> expressions)
         throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
   {
      int nullValues = 0;

      // map entries of uniqueGroup to list of expressions
      for (Field field : groupMembers)
      {
         Object fieldValue = getProperty(entity, field.getName());

         if (fieldValue == null)
         {
            nullValues++;
         }

         // create expression with corresponding fieldNames
         String expression = fieldValue == null ? "x.%s IS NULL" : "x.%s = :%s";
         expressions.add(String.format(expression, field.getName(), field.getName()));

         // collect fields in flat form for faster lookup
         parameters.put(field.getName(), fieldValue);
      }

      return nullValues;
   }

   /**
    * Finds a {@link Field} by a specific column name.
    * The name could be an attribute of an annotation or the property itself.
    *
    * @param allFieldsList all reflected fields
    * @param columnName    specific column name
    * @return the field corresponding to the column name
    */
   private Optional<Field> findFieldByName(List<Field> allFieldsList, String columnName)
   {
      return allFieldsList.stream().filter(field -> {

         boolean match;

         JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
         match = joinColumn != null && columnName.equals(joinColumn.name());

         if (!match)
         {
            Column column = field.getAnnotation(Column.class);
            match = column != null && columnName.equals(column.name());
         }

         match |= field.getName().equals(columnName);

         return match;

      }).findAny();
   }

   /**
    * Wrapper for restriction information of a field.
    */
   @Value
   class FieldRestriction
   {
      private boolean notNull;
      private Integer maxLength; // null = no restriction
   }


   /**
    * Wrapper for unique constraint restrictions of one field or multiple fields.
    */
   @Value
   class UniqueConstraintRestriction
   {
      private Map<String, Field> uniqueGroup;
   }
}
