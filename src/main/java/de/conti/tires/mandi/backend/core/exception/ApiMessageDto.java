package de.conti.tires.mandi.backend.core.exception;

import de.conti.tires.mandi.backend.core.validation.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Message dto to transfer messages to the frontend.
 */
public class ApiMessageDto
{
   @Getter
   private final Set<Message> global;
   @Getter
   private final Map<String, Set<Message>> fields;

   private ValidationErrors errors;
   private Set<ExceptionMessage> exceptionMessages;
   private boolean includeStacktrace;

   /**
    * Creates a new, empty message dto.
    */
   public ApiMessageDto()
   {
      this.global = new HashSet<>();
      this.fields = new HashMap<>();
      this.exceptionMessages = new HashSet<>();
      this.includeStacktrace = false;
   }

   /**
    * Creates a new message dto from {@link ValidationErrors}.
    *
    * @param errors the errors that should be transferred to the frontend
    */
   public ApiMessageDto(@NonNull ValidationErrors errors)
   {
      this();
      this.errors = errors;
      applyFromErrors();
   }

   /**
    * Creates a new message dto from the given exception.
    *
    * @param exception         the exception, that should be transferred
    * @param includeStacktrace flag to indicate, if the message should contain the stacktrace
    */
   public ApiMessageDto(@NonNull Exception exception, boolean includeStacktrace)
   {
      this();
      this.includeStacktrace = includeStacktrace;
      ExceptionMessage message = new ExceptionMessage(exception);
      this.exceptionMessages.add(message);
      applyFromExceptions();
   }

   /**
    * Creates a new message dto from the given {@link ExceptionMessage} set
    *
    * @param exceptionMessages the set of {@link ExceptionMessage} with exceptions for the frontend
    * @param includeStacktrace flag to indicate, if the messages should contain the stacktraces
    */
   public ApiMessageDto(@NonNull Set<ExceptionMessage> exceptionMessages, boolean includeStacktrace)
   {
      this();
      this.exceptionMessages = exceptionMessages;
      this.includeStacktrace = includeStacktrace;
      applyFromExceptions();
   }

   /**
    * Creates a new message dto from the given {@link ExceptionMessage}.
    *
    * @param exceptionMessage the {@link ExceptionMessage} for the frontend
    * @param includeStacktrace flag to indicate, if the message should contain the stacktrace
    */
   public ApiMessageDto(@NonNull ExceptionMessage exceptionMessage, boolean includeStacktrace)
   {
      this();
      this.exceptionMessages.add(exceptionMessage);
      this.includeStacktrace = includeStacktrace;
      applyFromExceptions();
   }

   /**
    * Fills the message structures from the given {@link ValidationErrors}.
    */
   private void applyFromErrors()
   {
      for (ValidationError error : errors.getGlobal())
      {
         this.global.add(new SimpleTranslatableMessage(MessageType.ERROR, error.getCode(),
               (Object[]) error.getParameters()));
      }

      for (Map.Entry<String, Set<ValidationError>> fieldErrors : errors.getFields().entrySet())
      {
         this.fields.putIfAbsent(fieldErrors.getKey(), new HashSet<>());
         for (ValidationError error : fieldErrors.getValue())
         {
            this.fields.get(fieldErrors.getKey()).add(new SimpleTranslatableMessage(MessageType.ERROR, error.getCode(),
                  (Object[]) error.getParameters()));
         }
      }
   }

   /**
    * Fills the message structure from the given exceptions.
    */
   private void applyFromExceptions()
   {
      for (ExceptionMessage message : this.exceptionMessages)
      {
         if (!this.includeStacktrace)
         {
            message.clearExceptionDetails();
         }
         this.global.add(message);
      }
   }

}
