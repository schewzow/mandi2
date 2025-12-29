package de.conti.tires.mandi.backend.core.validation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;


/**
 * Straight forward implementation of {@link Message}
 */
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMessage implements Message
{
   protected MessageType type;
   protected String message;

   /**
    * Creates a new message with the given {@link MessageType}.
    *
    * @param type value of {@link MessageType}
    */
   public SimpleMessage(@NonNull MessageType type)
   {
      this.type = type;
   }

   @Override
   public MessageType getType()
   {
      return this.type;
   }

   @Override
   public String getMessage()
   {
      return this.message;
   }

   @Override
   public void setMessage(@NonNull String message)
   {
      this.message = message;
   }

    /**
     * Get translation message based on code, parameters and locale
     */
    public String getTranslationMessage(String code, Object[] args, Locale locale)
    {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource == null ? code :
                messageSource.getMessage(code, args, locale);
    }
}
