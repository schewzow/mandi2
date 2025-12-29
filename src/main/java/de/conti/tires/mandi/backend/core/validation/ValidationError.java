package de.conti.tires.mandi.backend.core.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;


/**
 * A single validation error message.
 */
@EqualsAndHashCode
public class ValidationError implements Serializable
{
   private static final long serialVersionUID = 1L;

   /**
    * Message code / key.
    */
   @Getter
   private String code;
   /**
    * Localized message.
    */
   @Getter
   private Serializable[] parameters;

   /**
    * Creates a new instance.
    *
    * @param code       Error message key.
    * @param parameters Message parameters.
    */
   public ValidationError(String code, Serializable... parameters)
   {
      this.code = code;
      this.parameters = parameters;
   }
}
