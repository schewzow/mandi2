package de.conti.tires.mandi.container.config.spring;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractJacksonHttpMessageConverter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;


/**
 * Default configuration for Jackson's {@link ObjectMapper}.
 */
@Configuration
public class ObjectMapperConfiguration {
    /**
     * {@link Bean} resolver for Jackson's {@link ObjectMapper}.
     *
     * @param messageSource provider for translated texts
     * @return {@link ObjectMapper} {@link Bean}
     */
    @Bean
    public ObjectMapper objectMapper(MessageSource messageSource) {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
    }


    /**
     * Concrete implementation for Jackson 3 using the non-deprecated base class.
     */
    @Bean
    @Primary
    public AbstractJacksonHttpMessageConverter<?> customJacksonHttpMessageConverter(ObjectMapper objectMapper) {
        return new AbstractJacksonHttpMessageConverter<>(objectMapper,
                MediaType.APPLICATION_JSON,
                new MediaType("application", "*+json")) {
        };
    }
}
