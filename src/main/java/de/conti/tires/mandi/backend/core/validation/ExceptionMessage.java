package de.conti.tires.mandi.backend.core.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.conti.tires.mandi.backend.core.config.MandiConfig;
import de.conti.tires.mandi.backend.core.exception.ApiException;
import lombok.Getter;
import lombok.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;


/**
 * Translatable message for Exceptions.
 */
@Getter
public class ExceptionMessage extends SimpleTranslatableMessage
{
   /**
    * Default i18n-key to use if none was specified.
    */
   private static final String UNKNOWN_SERVER_ERROR_KEY = "error.UnknownServerError";

   private LocalDateTime dateTime;

   @JsonIgnore
   private Exception exception;

   @JsonInclude(JsonInclude.Include.NON_NULL)
   private String errorMessage;

   @JsonInclude(JsonInclude.Include.NON_NULL)
   private String exceptionName;

   @JsonInclude(JsonInclude.Include.NON_NULL)
   private String stacktrace;


   /**
    * Creates a new message of the given exception.
    * If the given Exception extends {@link ApiException},
    * the i18n-properties of the {@link ApiException} are used.
    *
    * @param exception the {@link Exception} to create the message from
    */
   public ExceptionMessage(@NonNull Exception exception)
   {
      super(MessageType.ERROR, UNKNOWN_SERVER_ERROR_KEY);
      this.dateTime = LocalDateTime.now();
      this.exception = exception;
      if (exception instanceof ApiException)
      {
         ApiException apiEx = (ApiException) exception;
         this.key = apiEx.getI18nKey();
         this.parameters = apiEx.getParams();
      }
      applyValues();
   }

   /**
    * Creates a new message of the given exception.
    * Will use the given key and params for i18n-translation.
    *
    * @param exception the exception to create the message from
    * @param key the i18n key to use
    * @param params the params for the message identified by the key
    */
   public ExceptionMessage(@NonNull Exception exception, @NonNull String key, Object... params)
   {
      super(MessageType.ERROR, key, params);
      this.dateTime = LocalDateTime.now();
      this.exception = exception;
      applyValues();
   }

   /**
    * Will set the exception specific values
    */
   private void applyValues()
   {
      this.exceptionName = this.exception.getClass().getSimpleName();
      this.errorMessage = this.exception.getMessage();

      StringWriter writer = new StringWriter();
      PrintWriter printer = new PrintWriter(writer);
      this.exception.printStackTrace(printer);
      this.stacktrace = writer.getBuffer().toString();
   }

   /**
    * Clears the exception details like stacktrace,
    * exception name and the error message of the exception.
    * This will be called, when exposeApiMessageDtoStacktrace in {@link MandiConfig} is set to {@code false}.
    */
   public void clearExceptionDetails()
   {
      this.stacktrace = null;
      this.errorMessage = null;
      this.exceptionName = null;
   }

}
