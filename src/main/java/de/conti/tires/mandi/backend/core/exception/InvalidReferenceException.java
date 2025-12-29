package de.conti.tires.mandi.backend.core.exception;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * A request using one or more invalid reference UUIDs occurred.
 */
public class InvalidReferenceException extends ApiException
{
   /**
    * Invalid UUIDs (may be not UUIDs at all) in case of collection resource operation. Might be (@code null).
    */
   private final transient Set<String> invalidCollectionReferences;
   /**
    * Invalid UUIDs related to single reference properties. Might be (@code null).
    */
   private final transient Set<InvalidPropertyReference> invalidPropertyReferences;

   /**
    * Creates an instance for a single invalid field reference.
    *
    * @param error error to add
    */
   public InvalidReferenceException(InvalidPropertyReference error)
   {
      super("error.IllegalReferenceException");
      this.invalidCollectionReferences = null;
      this.invalidPropertyReferences = new HashSet<>();
      invalidPropertyReferences.add(error);
   }

   /**
    * Creates a new exception using the standard error message.
    *
    * @param invalidCollectionReferences invalid UUIDs (may be not UUIDs at all) in case of collection resource operation.
    * @param invalidPropertyReferences   invalid UUIDs (may be not UUIDs at all) in case of collection resource operation.
    */
   public InvalidReferenceException(Collection<String> invalidCollectionReferences,
                                    Collection<InvalidPropertyReference> invalidPropertyReferences)
   {
      super("error.IllegalReferenceException");
      this.invalidCollectionReferences =
            invalidCollectionReferences == null ? null : new HashSet<>(invalidCollectionReferences);
      this.invalidPropertyReferences =
            invalidPropertyReferences == null ? null : new HashSet<>(invalidPropertyReferences);
   }

   /**
    * @return Invalid UUIDs (may be not UUIDs at all) in case of collection resource operation. Might be (@code null).
    */
   public Set<String> getInvalidCollectionReferences()
   {
      return invalidCollectionReferences;
   }

   /**
    * @return Invalid UUIDs related to single reference properties. Might be (@code null).
    */
   public Set<InvalidPropertyReference> getInvalidPropertyReferences()
   {
      return invalidPropertyReferences;
   }

   /**
    * Reference for single reference fields.
    */
   @AllArgsConstructor
   @Value
   @EqualsAndHashCode
   public static class InvalidPropertyReference implements Serializable
   {
      private static long serialVersionUUID = 1L;

      /**
       * related property
       */
      private String property;
      /**
       * invalid UUID (may be not UUIDs at all)
       */
      private String uuid;
   }
}
