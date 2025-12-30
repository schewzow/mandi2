package de.conti.tires.mandi.backend.core.exception;

/**
 * A method call, that requires an authenticated user, was done, but no authentication is available.
 */
public class NotAuthenticatedException extends BadRequestException
{
   /**
    * Creates a new instance.
    */
   public NotAuthenticatedException()
   {
      super("error.NotAuthenticatedException");
   }
}
