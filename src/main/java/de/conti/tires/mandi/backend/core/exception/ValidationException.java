package de.conti.tires.mandi.backend.core.exception;

import de.conti.tires.mandi.backend.core.validation.ValidationErrors;
import lombok.NonNull;

import java.util.Optional;


/**
 * Request is invalid because of validation constraint violation.
 */
public class ValidationException extends ApiException {
    /**
     * Error information
     */
    private final ValidationErrors errors;

    /**
     * Generates a validation exception for a single field error using a default message key.
     *
     * @param field field name
     */
    public ValidationException(@NonNull String field) {
        this(generateErrorsForSingleField(field, null));
    }

    /**
     * Generates a validation exception for a single field error.
     *
     * @param field      field name
     * @param message    optional message key - defaults to "invalid value"
     * @param parameters message parameters
     */
    public ValidationException(@NonNull String field, String message, @NonNull Object... parameters) {
        this(generateErrorsForSingleField(field, message, parameters));
    }

    /**
     * Creates a new instance based on the given error information.
     *
     * @param errors causing errors
     */
    public ValidationException(ValidationErrors errors) {
        super("error.ValidationException");
        this.errors = errors;
    }

    private static ValidationErrors generateErrorsForSingleField(@NonNull String field, String message,
                                                                 @NonNull Object... parameters) {
        ValidationErrors errors = new ValidationErrors();
        errors.addFieldError(field, Optional.ofNullable(message).orElse("error.validation.invalidValue"), parameters);
        return errors;
    }

    /**
     * @return error information
     */
    public ValidationErrors getErrors() {
        return this.errors;
    }
}
