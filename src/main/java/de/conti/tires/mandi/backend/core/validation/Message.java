package de.conti.tires.mandi.backend.core.validation;

/**
 * Defines a message
 */
public interface Message
{
   /**
    * @return message type
    */
   MessageType getType();

   /**
    *
    * @return message text
    */
   String getMessage();

   /**
    * Sets the message text.
    *
    * @param message the text of the message
    */
   void setMessage(String message);
}
