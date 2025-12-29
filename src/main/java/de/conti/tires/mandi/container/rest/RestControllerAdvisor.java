package de.conti.tires.mandi.container.rest;

import de.conti.tires.mandi.backend.core.config.MandiConfig;
import de.conti.tires.mandi.backend.core.exception.*;
import de.conti.tires.mandi.backend.core.validation.ExceptionMessage;
import de.conti.tires.mandi.backend.core.validation.ValidationErrors;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * Spring advisor to map exceptions to a proper REST response for all REST controller.
 */
@Log4j2
@RestControllerAdvice
public class RestControllerAdvisor
{
   /**
    * Gomes application configuration parameters ({@link MandiConfig}).
    */
   private final MandiConfig mandiConfig;

   /**
    * Component constructor.
    *
    * @param mandiConfig required service/component
    */
   @Autowired
   public RestControllerAdvisor(@NonNull MandiConfig mandiConfig)
   {
      this.mandiConfig = mandiConfig;
   }

   /**
    * Handles 404.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler({ ResourceNotFoundException.class, EntityNotFoundException.class })
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public ApiMessageDto handleResourceNotFoundException(@NonNull Exception exception)
   {
      return createApiMessage(exception, "error.ResourceNotFoundException");
   }

   /**
    * Handles validation errors.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler(ValidationException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ApiMessageDto handleValidationException(@NonNull ValidationException exception)
   {
      return createApiMessage(exception.getErrors(), exception);
   }

   /**
    * Handles reference errors.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler(InvalidReferenceException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ApiMessageDto handleInvalidReferenceException(@NonNull InvalidReferenceException exception)
   {
      ValidationErrors errors = new ValidationErrors();
      Optional.ofNullable(exception.getInvalidCollectionReferences()).ifPresent(
            error -> errors.addGlobalError("error.invalidref.collection", StringUtils.join(error.toArray(), ", ")));
      Optional.ofNullable(exception.getInvalidPropertyReferences()).ifPresent(error -> error
            .forEach(err -> errors.addFieldError(err.getProperty(), "error.invalidref.single", err.getUuid())));

      return createApiMessage(errors, exception);
   }

//   /**
//    * Handles elastic search errors that are caused by invalid queries.
//    *
//    * @param exception exception to handle
//    * @return error object
//    */
//   @ExceptionHandler(SearchPhaseExecutionException.class)
//   @ResponseStatus(HttpStatus.BAD_REQUEST)
//   public ApiMessageDto handleElasticSearchQueryException(@NonNull SearchPhaseExecutionException exception)
//   {
//      return createApiMessage(exception, "error.SearchPhaseExecutionException");
//   }

   /**
    * Handles general bad request exceptions.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler(BadRequestException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ApiMessageDto handleGeneralBadRequestException(@NonNull BadRequestException exception)
   {
      return createApiMessage(exception);
   }

   /**
    * Handles generic exception.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler(GenericException.class)
   public ResponseEntity<ApiMessageDto> handleGenericException(@NonNull GenericException exception)
   {
      return ResponseEntity.status(exception.getStatusCode()).body(createApiMessage(exception));
   }

   /**
    * Handles Spring / Java argument validation triggered errors.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler({ MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class,
         MethodArgumentConversionNotSupportedException.class })
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ApiMessageDto handleMethodArgumentNotValidException(@NonNull Exception exception)
   {
      return handleInternalServerError(exception);
   }

   /**
    * Handles HTTP message conversions errors.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler(HttpMessageNotReadableException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ApiMessageDto handleHttpMessageNotReadableException(@NonNull HttpMessageNotReadableException exception)
   {
      return handleInternalServerError(exception);
   }

   /**
    * Handles 500.
    *
    * @param exception exception to handle
    * @return error object
    */
   @ExceptionHandler(Exception.class)
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ApiMessageDto handleInternalServerError(@NonNull Exception exception)
   {
      // Let spring handle it's own exceptions
      rethrowSpringExceptions(exception);

      log.debug(String.format("%s will be handled by RestControllerAdvisor.", exception.getClass().getSimpleName()));
      return createApiMessage(exception);
   }

   /**
    * This method makes sure that we don't collide with springs exception handling.
    *
    * @param exception Exception that possibly handled by spring.
    */
   private void rethrowSpringExceptions(@NonNull Exception exception)
   {
      Stream.of(AccessDeniedException.class, MethodNotAllowedException.class, AuthenticationException.class)
            .filter(exceptionClass -> exceptionClass.isAssignableFrom(exception.getClass()))
            .findFirst()
            .ifPresent(e ->
            {
               log.debug(String.format("Encountered %s. Rethrowing exception to let spring handle it.",
                     exception.getClass().getSimpleName()), exception);
               throw (RuntimeException) exception;
            });
   }

   /**
    * Creates a new {@link ApiMessageDto} of the given exception.
    * Will also evaluate if the stacktrace should be exposed and log the exception.
    *
    * @param exception the exception, to create the message of
    * @return message for the given exception
    */
   private ApiMessageDto createApiMessage(@NonNull Exception exception)
   {
      log.error(exception);
      return new ApiMessageDto(exception, mandiConfig.isExposeApiMessageDtoStacktrace());
   }

   /**
    * Creates a new {@link ApiMessageDto} of the given exception with the given key and parameters.
    * Will also evaluate if the stacktrace should be exposed and log the exception.
    *
    * @param exception the exception, to create the message of
    * @param key       the i18n key to use
    * @param params    optional parameters to use for the message
    * @return message for the given exception
    */
   private ApiMessageDto createApiMessage(@NonNull Exception exception, @NonNull String key, Object... params)
   {
      log.error(exception);
      ExceptionMessage message = new ExceptionMessage(exception, key, params);
      return new ApiMessageDto(message, mandiConfig.isExposeApiMessageDtoStacktrace());
   }

   /**
    * Creates a new {@link ApiMessageDto} of the given validation errors.
    * Will also log the exception.
    *
    * @param errors    validation errors that should be supplied to the front end
    * @param exception the exception to log
    * @return message for the given errors
    */
   private ApiMessageDto createApiMessage(@NonNull ValidationErrors errors, @NonNull Exception exception)
   {
      log.error(exception);
      return new ApiMessageDto(errors);
   }

}
