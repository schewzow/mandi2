package de.conti.tires.mandi.backend.core.validation;

import java.util.Map;


/**
 * Validator for domain objects / entities.
 *
 * @param <E> entity type
 */
public interface Validator<E>
{
   /**
    * Validates the given entity and writes found validation errors in the given error information object.
    * <p>
    * {@code errors} is the only parameter that is allowed to be modified.
    *
    * @param entity        entity to validate
    * @param previousState entity's previous state ({@code null} in case of creation)
    * @param data          change data
    * @param errors        error information to fill (I18N bean is available)
    */
   void validate(E entity, E previousState, Map<String, Object> data,
         ValidationErrors errors);
}
