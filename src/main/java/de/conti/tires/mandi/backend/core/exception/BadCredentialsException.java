package de.conti.tires.mandi.backend.core.exception;

/**
 * Sign in failure.
 */
public class BadCredentialsException extends BadRequestException
{
   /**
    * Creates a new instance.
    */
   public BadCredentialsException()
   {
      super("error.BadCredentials");
   }
}
