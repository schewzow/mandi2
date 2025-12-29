package de.conti.tires.mandi.backend.core.base;

import de.conti.tires.mandi.backend.core.exception.ApiException;
import de.conti.tires.mandi.backend.core.exception.InvalidReferenceException;
import de.conti.tires.mandi.backend.core.exception.ValidationException;
import de.conti.tires.mandi.backend.core.validation.ValidationErrors;
import de.conti.tires.mandi.backend.core.validation.Validator;
import de.conti.tires.mandi.backend.util.ComplexFieldsModelMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.MappingException;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

//todo get rid of the baseservice and use static methods with corresponding properties


@Log4j2
public class ServiceUtils {


    public static <E extends BaseEntity> void performValidation(Map<String, Object> payload, E entity, boolean create,
                                                                E oldState, Validator<E> validator)
            throws InvalidReferenceException, AccessDeniedException, ValidationException {

        ValidationErrors errors = new ValidationErrors();
        Optional.ofNullable(validator).ifPresent(validatr -> validatr.validate(entity, oldState, payload, errors));

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // just to create a global error in backend for testing...
//        Set<String> invalidCollectionReferences = new HashSet<>();
//        Set<InvalidReferenceException.InvalidPropertyReference> invalidPropertyReferences = new HashSet<>();
//        invalidCollectionReferences.add("roles");
//        throw new InvalidReferenceException(invalidCollectionReferences, invalidPropertyReferences);
    }


//   public static<E extends BaseEntity> void notifyHandler (boolean create, E entity,
//         Map<String, Object> payload, E oldState,
//         DomainObjectEventHandler domainObjectEventHandler)
//   {
//      DomainObjectEvent event;
//      if (create)
//      {
//         event = DomainObjectEvent.created(entity);
//      }
//      else
//      {
//         event = DomainObjectEvent.updated(entity, extractProperties(oldState, payload.keySet()));
//      }
//      domainObjectEventHandler.handleEvent(event);
//
//   }


    private static <E extends BaseEntity> Map<String, Object> extractProperties(@NonNull E oldState, @NonNull Set<String> properties) {
        Map<String, Object> values = new HashMap<>(properties.size());
        for (String property : properties) {
            try {
                values.put(property, PropertyUtils.getProperty(oldState, property));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.debug("Could not read property " + property, e);
            }
        }
        return values;
    }


    /**
     * Updates the target entity from the patch.
     * <p>
     * Single {@link BaseEntity} references have to be delivered as UUIDs. Collections are ignored.
     * <p>
     * The default implementation does not check for invalid fields and ignores such instead.
     *
     * @param payload payload to process (not empty, not {@code null})
     * @param target  target to update from payload (never {@code null})
     * @throws InvalidReferenceException single reference is no UUID or referenced entity not found
     * @throws ValidationException       a value was sent can not be transformed to the target structure
     */
    public static <E extends BaseEntity> void performPatch(@NotEmpty Map<String, Object> payload, @NotNull E target,
                                                           EntityManagerFactory entityManagerFactory, EntityManager entityManager)
            throws InvalidReferenceException, ValidationException {
        // map simple fields
        mapSimpleFields(payload, target);

        // calculate single reference information

        Map<Class<? extends BaseEntity>, EntityUUIDs> entityUuids = new HashMap<>();
        Set<String> propertiesToNull = new HashSet<>();

        List<InvalidReferenceException.InvalidPropertyReference> invalidRefs = new ArrayList<>();

        Metamodel metamodel = entityManagerFactory.getMetamodel();
        metamodel.entity(target.getClass()).getAttributes().stream()
                .filter(
                        attribute -> BaseEntity.class.isAssignableFrom(attribute.getJavaType()) && !attribute.isCollection()
                                && payload.containsKey(attribute.getName()))
                .forEach(attribute ->
                {
                    String property = attribute.getName();

                    Object uuidObject = payload.get(property);
                    if (uuidObject == null) {
                        propertiesToNull.add(property);
                    } else {
                        try {
                            UUID uuid = UUID.fromString((String) uuidObject);

                            Class<? extends BaseEntity> type = (Class<? extends BaseEntity>) attribute.getJavaType();
                            EntityUUIDs uuids = entityUuids.get(type);
                            if (uuids == null) {
                                uuids = new EntityUUIDs(metamodel.entity(type).getName(), type);
                                entityUuids.put(type, uuids);
                            }

                            uuids.getUuids().add(new PropertyUuid(property, uuid));
                        } catch (ClassCastException | IllegalArgumentException e) {
                            log.trace("found invalid single ref in request", e);
                            invalidRefs.add(new InvalidReferenceException.InvalidPropertyReference(property,
                                    uuidObject.toString()));
                        }
                    }
                });

        ResolvedEntityInformation resolvedUUIDs = resolveReferencedEntities(entityUuids.values(), entityManager);
        invalidRefs.addAll(resolvedUUIDs.getInvalidRefs());

        if (!invalidRefs.isEmpty()) {
            throw new InvalidReferenceException(null, invalidRefs);
        }

        // update single reference data

        try {
            for (String prop : propertiesToNull) {
                BeanUtils.setProperty(target, prop, null);
            }
            for (Map.Entry<String, Object> entry : resolvedUUIDs.getData().entrySet()) {
                BeanUtils.setProperty(target, entry.getKey(), entry.getValue());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("setting ref property failed", e);
            throw new ApiException(e);
        }
    }

    /**
     * Maps all 'simple' fields (no {@link UuidIdentifiable} references) from the payload to the entity.
     *
     * @param payload patch/create map
     * @param target  target entity
     * @throws ValidationException in case of a type error
     */
    @SuppressWarnings("squid:S135") // "Fail early" like continue is acceptable here
    private static <E extends BaseEntity> void mapSimpleFields(@NonNull Map<String, Object> payload, @NonNull E target) throws ValidationException {
        ComplexFieldsModelMapper mapper = new ComplexFieldsModelMapper(target);

        ValidationErrors typeErrors = new ValidationErrors();

        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String property = entry.getKey();
            Object value = entry.getValue();

            if (property.contains(".")) {
                log.trace("Ignoring nested property {}.", property);
                continue;
            }

            // map empty strings to null
            if ("".equals(value)) {
                value = null;
            }

            boolean error = false;

            Class<?> propertyType;

            try {
                propertyType = PropertyUtils.getPropertyType(target, property);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.trace("Could not access field " + property + " - ignoring.", e);
                propertyType = null;
            }

            // skip not accessible properties and references
            if (propertyType == null || UuidIdentifiable.class.isAssignableFrom(propertyType)) {
                continue;
            }

            // primitive not null validation
            if (propertyType.isPrimitive() && value == null) {
                typeErrors.addFieldError(property, "error.validation.required");
            }
            // validate enum
            else if (Enum.class.isAssignableFrom(propertyType) && !(validateEnum(propertyType, value))) {
                error = true;
            }
            // handle any other field via mapper
            else {
                try {
                    Object destValue = (value == null) ? null : mapper.map(value, propertyType);
                    PropertyUtils.setProperty(target, property, destValue);
                } catch (MappingException me) {
                    log.trace("mapping failed for " + property, me);
                    error = true;
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.warn("could not write field " + property + " - ignoring", e);
                }
            }

            // if field error was resolved add message
            if (error) {
                typeErrors.addFieldError(property, "error.validation.invalidValue");
            }
        }

        if (!(typeErrors.isEmpty())) {
            throw new ValidationException(typeErrors);
        }
    }

    private static boolean validateEnum(@NonNull Class<?> propertyType, Object value)
            throws DateTimeParseException {
        if (value == null) {
            return true;
        }

        boolean error = false;
        if (value instanceof String) {
            error = Arrays.stream(((Class<? extends Enum<?>>) propertyType).getEnumConstants())
                    .noneMatch(item -> item.name().equals(value));
        } else if (!(value.getClass().equals(propertyType))) {
            error = true;
        }
        return !(error);
    }

    /**
     * Holds an entity's entity name and the collected UUIDs.
     */
    @Value
    @RequiredArgsConstructor
    public static class EntityUUIDs {
        @NonNull
        private String entityName;
        @NonNull
        private Class<? extends BaseEntity> entityClass;
        private Set<PropertyUuid> uuids = new HashSet<>();
    }

    /**
     * Combines a property name and a UUID value.
     */
    @RequiredArgsConstructor
    @Value
    @EqualsAndHashCode
    protected static class PropertyUuid {
        private String property;
        private UUID uuid;
    }

    /**
     * Load entities for referenced UUIDs.
     *
     * @param uuidInformation collected UUIDs per class
     * @return resulting information
     */
    private static ResolvedEntityInformation resolveReferencedEntities(
            Collection<EntityUUIDs> uuidInformation, EntityManager entityManager) {
        ResolvedEntityInformation result = new ResolvedEntityInformation();

        uuidInformation.stream()
                .filter(info -> CollectionUtils.isNotEmpty(info.getUuids()))
                .forEach(info ->
                {
                    TypedQuery<? extends BaseEntity> query = entityManager
                            .createQuery("select e from " + info.getEntityName() + " e where e.uuid in (:uuids)",
                                    info.getEntityClass());
                    query.setParameter("uuids", info.getUuids().stream()
                            .map(PropertyUuid::getUuid)
                            .collect(Collectors.toSet()));

                    // allows the AnnotationBasedValidator to evaluate before the repository throws unexpected exceptions
                    query.setFlushMode(FlushModeType.COMMIT);

                    Set<PropertyUuid> unresolved = new HashSet<>(info.getUuids());
                    query.getResultList().forEach(entity ->
                            info.getUuids().stream()
                                    .filter(val -> val.getUuid().equals(entity.getUuid()))
                                    .forEach(propUuid ->
                                    {
                                        unresolved.remove(propUuid);
                                        result.addMapping(propUuid.getProperty(), entity);
                                    }));

                    unresolved.forEach(result::addError);
                });

        return result;
    }

    /**
     * Holds property to entity mapping and invalid UUID information.
     */
    @RequiredArgsConstructor
    @Value
    protected static class ResolvedEntityInformation {
        private Map<String, Object> data = new HashMap<>();
        private List<InvalidReferenceException.InvalidPropertyReference> invalidRefs = new ArrayList<>();

        /**
         * Adds a resolved value.
         *
         * @param property property name
         * @param entity   value entity
         */
        public void addMapping(String property, Object entity) {
            this.data.put(property, entity);
        }

        /**
         * Adds a not resolvable UUID.
         *
         * @param error information
         */
        public void addError(PropertyUuid error) {
            invalidRefs.add(new InvalidReferenceException.InvalidPropertyReference(error.getProperty(),
                    error.getUuid().toString()));
        }
    }

}

