package de.conti.tires.mandi.container.config.spring.locale;

import de.conti.tires.mandi.container.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;
import java.util.Optional;


/**
 * Interceptor that sets the current locale based on the current  user - if available.
 */
@Log4j2
@RequiredArgsConstructor
public class LocaleInterceptor implements HandlerInterceptor {
    public static final Locale DEFAULT_LOCALE = Locale.US;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object principal;
        try {
            principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (NullPointerException e) {
            log.debug("No current principal found.");
            log.trace("NPE occurred during resolving principle.", e);
            return true;
        }
        if (!UserDetailsImpl.class.isAssignableFrom(principal.getClass())) {
            log.debug("Current principal is not assignable to UserDetailsImpl.");
            return true;
        }

        UserDetailsImpl user = (UserDetailsImpl) principal;
        Optional<Locale> locale = Optional.ofNullable(user.getLanguage())
                .filter(StringUtils::isNotEmpty)
                .map(Locale::forLanguageTag);

        LocaleContextHolder.setLocale(locale.orElse(DEFAULT_LOCALE));
        if (response != null) {
            response.setLocale(locale.orElse(DEFAULT_LOCALE));
        }
        return true;
    }
}
