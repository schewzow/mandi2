package de.conti.tires.mandi.container.config.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;


/**
 * Default configuration for Jackson's {@link JsonMapper}.
 */
@Configuration
public class JacksonConfig {
    /**
     * {@link Bean} resolver for Jackson's {@link JsonMapper}.
     *
     * @return {@link ObjectMapper (or JsonMapper in Jackson3)} {@link Bean}
     */
    @Bean
    //public JsonMapper jsonMapper(MessageSource messageSource) {
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
    }


    /**
     * Concrete implementation for Jackson 3 using the non-deprecated base class.
     * It shall be used only if translation service during Jackson serialization shall be used.
     */
//    @Bean
//    @Primary
//    public AbstractJacksonHttpMessageConverter<?> customJacksonHttpMessageConverter(ObjectMapper objectMapper) {
//        return new AbstractJacksonHttpMessageConverter<>(objectMapper,
//                MediaType.APPLICATION_JSON,
//                new MediaType("application", "*+json")) {
//        };
//    }
}
