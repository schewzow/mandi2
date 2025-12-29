package de.conti.tires.mandi.container.config.spring.locale;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Locale handling.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocaleConfiguration implements WebMvcConfigurer
{
   @Override
   public void addInterceptors(InterceptorRegistry registry)
   {
      registry.addInterceptor(new LocaleInterceptor());
   }
}
