package de.conti.tires.mandi.backend.util;

import de.conti.tires.mandi.backend.core.base.UuidIdentifiable;
import lombok.NonNull;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * String related helper functions.
 */
public class StringUtils
{
   /**
    * Text to append to oversized trimmed texts when using {@link #trimToLength(String, int)}
    */
   public static final String ELLIPSIS = "...";

   /**
    * Generates a typical "Entity with UUID 'uuid' not found!" String.
    * <p>
    * example usage: StringUtils.generateNotFoundString(MixingOrderEntity.class,
    * UUID.fromString("179c9d28-07dc-46a6-8606-100e3d2d2eca"))
    * <p>
    * will generate: "MixingOrder with UUID '179c9d28-07dc-46a6-8606-100e3d2d2eca' not found!"
    * <p>
    * If the given class ends with "Entity" then this "Entity" string will be cut off in the generated string.
    *
    * @param clazz the entity class for which the given uuid was not found
    * @param uuid  the uuid of the entity that was not found
    * @return String of format "Entity with UUID 'uuid' not found!"
    */
   public static String generateNotFoundString(@NonNull final Class<? extends UuidIdentifiable> clazz,
         @NonNull final UUID uuid)
   {
      String entityName = clazz.getSimpleName();
      if (entityName.endsWith("Entity"))
      {
         entityName = cutOffEntityString(entityName);
      }
      return String.format("%s with UUID '%s' not found!", entityName, uuid.toString());
   }

   private static String cutOffEntityString(@NonNull final String entityNameString)
   {
      return entityNameString.substring(0, entityNameString.length() - "Entity".length());
   }

   /**
    * Joins all given non-{@code null} elements with the given separator.
    *
    * @param separator separator, e.g. ", "
    * @param parts     parts to join, will call {@link Object#toString()}, will skip {@code null}s
    * @return resulting text
    */
   public static String joinNonNulls(@NonNull String separator, Object... parts)
   {
      return org.apache.commons.lang3.StringUtils.join(
            Stream.of(parts).filter(Objects::nonNull).collect(Collectors.toList()),
            separator);
   }

   /**
    * Trims the given text to the specified maximal length using {@link #ELLIPSIS}.    *
    *
    * @param text      text to trim ({@code null} safe)
    * @param maxLength maximal allowed length (must be greater {@link #ELLIPSIS} length)
    * @return original text if not violating the maximal length, trimmed text otherwise
    */
   public static String trimToLength(String text, int maxLength)
   {
      if (text == null || text.length() <= maxLength)
      {
         return text;
      }

      return text.substring(0, maxLength - ELLIPSIS.length()) + ELLIPSIS;
   }

   /**
    * Replace all 0 by O to make numbers with mono space font better readable.
    * @param toReplace input string
    * @return output string
    */
   public static String replaceZeroWithO(String toReplace)
   {
      return toReplace.replace('0','O');
   }
}
