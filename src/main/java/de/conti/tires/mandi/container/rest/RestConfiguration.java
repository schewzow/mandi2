package de.conti.tires.mandi.container.rest;


import de.conti.tires.mandi.backend.laboratory.LaboratoryEntity;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
                                                     CorsRegistry cors) {
        //HttpMethod[] unsupportedActions = {HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH};

        // remove _embedded
        //config.setDefaultMediaType(MediaType.APPLICATION_JSON);
        //config.useHalAsDefaultJsonMediaType(false);


        config.exposeIdsFor(LaboratoryEntity.class);

//        disableHttpMethods(Book.class, config, unsupportedActions);
//        disableHttpMethods(Review.class, config, unsupportedActions);

        /* Configure CORS mapping */
//        cors.addMapping(config.getBasePath() + "/**")
//                .allowedOrigins("http://localhost:8080", "http://localhost:5173", "https://xxxxx.eu")
//                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//        ;
    }
}
