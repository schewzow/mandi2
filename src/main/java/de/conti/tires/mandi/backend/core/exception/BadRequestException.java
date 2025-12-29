package de.conti.tires.mandi.backend.core.exception;

/**
 * Base class of HTTP response code 400 exceptions with a simple parameterized error message.
 * Extending this exception will automatically be recognized.
 * <p>
 * If instantiated directly informing about invalid payload.
 */
public class BadRequestException extends ApiException
{
   /**
    * Creates an instance informing that the request payload is not valid.
    */
   public BadRequestException()
   {
      super("error.BadRequestException");
   }

   /**
    * Creates an instance informing that the request payload is not valid.
    *
    * @param cause causing exception
    */
   public BadRequestException(Throwable cause)
   {
      this();
      initCause(cause);
   }

   /**
    * Just delegating to parent constructor.
    *
    * @param message    error message key
    * @param parameters optional message parameters
    */
   protected BadRequestException(String message, Object... parameters)
   {
      super(message, parameters);
   }
}
