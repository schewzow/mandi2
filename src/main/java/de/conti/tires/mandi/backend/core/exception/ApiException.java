package de.conti.tires.mandi.backend.core.exception;

import de.conti.tires.mandi.container.util.StaticContextAccessor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;


/**
 * Root class of all REST API exceptions.
 */
public class ApiException extends RuntimeException {
    @Getter
    private final String i18nKey;

    @Getter
    private final transient Object[] params;

    /**
     * Constructs a new exception from a cause.
     *
     * @param cause causing issue
     */
    public ApiException(Throwable cause) {
        super(cause);
        this.i18nKey = "error.ApiException";
        this.params = null;
    }

    /**
     * Constructs a new exception with key and message parameter.
     *
     * @param key    lookup key
     * @param params parameter used in the message
     */
    public ApiException(String key, Object... params) {
        this.i18nKey = key;
        this.params = params;
    }

    @Override
    public String getMessage() {
        MessageSource messageSource = StaticContextAccessor.getBean(MessageSource.class);

        if (messageSource != null) {
            String message = messageSource.getMessage(this.i18nKey, this.params, this.i18nKey, LocaleContextHolder.getLocale());
            return messageSource.getMessage(this.i18nKey, this.params, this.i18nKey, LocaleContextHolder.getLocale());
        } else {
            return this.i18nKey;
        }
    }
}
