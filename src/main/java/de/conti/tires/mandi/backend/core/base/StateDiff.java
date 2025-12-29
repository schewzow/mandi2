package de.conti.tires.mandi.backend.core.base;

import de.conti.tires.mandi.backend.core.doe.EventType;
import lombok.Getter;

import java.util.Objects;


/**
 * Holds the old state and the new state of an UUID  identifiable object (logically the same instance related to UUID).
 * Expects the two entities to be the same logical instance.
 *
 * @param <E> object type
 */
@Getter
public class StateDiff<E extends UuidIdentifiable>
{
   /**
    * Former object state. Detached copy. {@code null} in case of an object creation.
    */
   private E previousState;
   /**
    * Current object state. {@code null} in case of an object deletion.
    */
   private E currentState;

   /**
    * Creates a new state diff.
    * <p>
    * Supports create (currentState set only), update (both set) and delete (previousState set only).
    *
    * @param previousState old state
    * @param currentState  new state
    * @throws IllegalArgumentException both arguments are {@code null} or UUIDs do not match
    */
   public StateDiff(E previousState, E currentState) throws IllegalArgumentException
   {
      if (previousState == null && currentState == null)
      {
         throw new IllegalArgumentException("null for both states is not allowed");
      }

      if (previousState != null && currentState != null
            && !Objects.equals(previousState.getUuid(), currentState.getUuid()))
      {
         throw new IllegalArgumentException(
               "uuid " + previousState.getUuid() + " does not match uuid " + currentState.getUuid());
      }

      this.previousState = previousState;
      this.currentState = currentState;
   }

   /**
    * @return event type resulting from this diff
    */
   public EventType getEventType()
   {
      if (previousState == null)
      {
         return EventType.CREATE;
      }
      else if (currentState == null)
      {
         return EventType.DELETE;
      }
      else
      {
         return EventType.UPDATE;
      }
   }
}
