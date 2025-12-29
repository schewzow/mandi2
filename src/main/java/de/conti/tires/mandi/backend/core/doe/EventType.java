package de.conti.tires.mandi.backend.core.doe;

import lombok.NonNull;


/**
 * Enum for event typing
 */
public enum EventType
{
   CREATE,
   UPDATE,
   DELETE;

   /**
    * Returns specific mapped label as string.
    *
    * @param type must not be {@literal null}
    * @return specific mapped label, otherwise {@literal null}
    */
   public static String getLabelByType(@NonNull EventType type)
   {
      switch (type)
      {
         case CREATE:
            return "created";
         case DELETE:
            return "deleted";
         case UPDATE:
            return "modified";
         default:
            return null;
      }
   }
}
