package de.conti.tires.mandi.backend.core.base;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;


/**
 * Holds information related to an object state transition after field changes.
 *
 * @param <E> entity type
 */
@Getter
@Log4j2
public class EntityDiff<E extends BaseEntity> extends StateDiff<E>
{
   /**
    * Entity type.
    */
   private final Class<E> entityClass;
   /**
    * Original change data of the POST or PATCH request. Never {@code null}.
    */
   private final Map<String, Object> data;

   /**
    * Creates a new instance.
    *
    * @param entityClass   entity type
    * @param previousState old state (@code null} in case of a new object
    * @param currentState  new state (@code null} in case of a deletion
    * @param data          original change data of POST or PATCH request - if {@code null} an empty map will be created
    * @throws IllegalArgumentException entityClass is {@code null} or no state is set
    */
   public EntityDiff(Class<E> entityClass, E previousState, E currentState, Map<String, Object> data)
         throws IllegalArgumentException
   {
      super(previousState, currentState);
      this.entityClass = entityClass;
      this.data = data != null ? data : new HashMap<>();
   }

//   /**
//    * Creates a new instance based on a domain object event instance.
//    *
//    * @param event       source
//    * @param entityClass related entity class
//    * @param <E>         entity type
//    * @return corresponding diff object
//    */
//   @SuppressWarnings("unchecked")
//   public static <E extends BaseEntity> EntityDiff<E> createFromDomainObjectEvent(@NonNull DomainObjectEvent event,
//         @NonNull Class<E> entityClass)
//   {
//      E entity = (E) event.getEntity();
//
//      switch (event.getType())
//      {
//         case CREATE:
//            return new EntityDiff<>(entityClass, null, entity, null);
//         case DELETE:
//            return new EntityDiff<>(entityClass, entity, null, null);
//         default: // UPDATE
//            return new EntityDiff<>(entityClass, oldState(event), entity, event.getOldValues());
//      }
//   }
//
//   @SuppressWarnings("unchecked")
//   private static <E extends BaseEntity> E oldState(@NonNull DomainObjectEvent event)
//   {
//      E entity = (E) CloneUtils.shallowBeanCopy(event.getEntity());
//      event.getOldValues().forEach((field, value) ->
//      {
//         try
//         {
//            PropertyUtils.setProperty(entity, field, value);
//         }
//         catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
//         {
//            if (Stream.of(FORCE_EXPORT_PROPERTY, CELINE_IMPORT_ACTIVE, CELINE_IMPORT_GOMES_MOD)
//                  .noneMatch(virtualField -> virtualField.equals(field)))
//            {
//               log.warn("could not write old value", e);
//            }
//         }
//      });
//      return entity;
//   }
}
