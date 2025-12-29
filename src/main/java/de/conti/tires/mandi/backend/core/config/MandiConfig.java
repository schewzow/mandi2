package de.conti.tires.mandi.backend.core.config;

import de.conti.tires.mandi.backend.core.exception.ApiMessageDto;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


/**
 * Gomes Config DTO extracted from properties.
 */
@Data
@Valid
@Validated
@Configuration
@ConfigurationProperties(prefix = "mandi")
public class MandiConfig
{
   /**
    * Include stacktrace in {@link ApiMessageDto}.
    */
   private boolean exposeApiMessageDtoStacktrace = false;

}
