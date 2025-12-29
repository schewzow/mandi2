package de.conti.tires.mandi.backend.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;


/**
 * Replacing {@link ResourceNotFoundException} in business level.
 */
@AllArgsConstructor
public class EntityNotFoundException extends IllegalArgumentException
{
   @Getter
   private final Object requestedUuid;
}
