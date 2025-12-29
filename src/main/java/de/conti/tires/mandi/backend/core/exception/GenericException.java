package de.conti.tires.mandi.backend.core.exception;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;


/**
 * An exception providing translation message information and the HTTP status code to send.
 */
public class GenericException extends ApiException
{
   @Getter final int statusCode;

   /**
    * Create a new instance.
    *
    * @param status HTTP status code to send
    * @param key    error message key
    * @param params error message parameters
    */
   public GenericException(@NonNull HttpStatus status, @NonNull String key, Object... params)
   {
      this(status.value(), key, params);
   }

   /**
    * Create a new instance.
    *
    * @param statusCode HTTP status code to send
    * @param key        error message key
    * @param params     error message parameters
    */
   public GenericException(int statusCode, @NonNull String key, Object... params)
   {
      super(key, params);
      this.statusCode = statusCode;
   }
}
