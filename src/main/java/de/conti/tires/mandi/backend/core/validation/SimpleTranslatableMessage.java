package de.conti.tires.mandi.backend.core.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;


/**
 * Implements the {@link TranslatableMessage}
 */
@Getter
@NoArgsConstructor
public class SimpleTranslatableMessage extends SimpleMessage implements TranslatableMessage
{
   /**
    * I18N key
    */
   protected String key;

   /**
    * Message parameters.
    */
   @Getter
   @JsonIgnore
   protected Object[] parameters;

   /**
    * Creates a new message with the given type, i18n-key and parameters.
    *
    * @param type       message type
    * @param key        translation key
    * @param parameters optional message parameters
    */
   public SimpleTranslatableMessage(@NonNull MessageType type, @NonNull String key, Object... parameters)
   {
      super(type);
      this.key = key;
      this.parameters = parameters;
   }

}


