package de.conti.tires.mandi.backend.core.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;


/**
 * Straight forward implementation of {@link Translatable}. Parameters will not be exposed by Jackson.
 */
public class TranslatableImpl implements Translatable
{
   @NonNull
   private final String key;
   @NonNull
   private final Object[] parameters;

   /**
    * Creates a new message.
    *
    * @param key message key
    * @param parameters message parameters
    */
   public TranslatableImpl(@NonNull String key, @NonNull Object... parameters)
   {
      this.key = key;
      this.parameters = parameters;
   }

   @Override
   public String getKey()
   {
      return key;
   }

   @Override
   @JsonIgnore
   public Object[] getParameters()
   {
      return parameters;
   }
}
