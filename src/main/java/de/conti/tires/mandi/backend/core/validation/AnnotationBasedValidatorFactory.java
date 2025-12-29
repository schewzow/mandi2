package de.conti.tires.mandi.backend.core.validation;

import de.conti.tires.mandi.backend.core.base.BaseEntity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * Factory to get generic instances of {@link AnnotationBasedValidator}. All instances will be cached in a map.
 */
@Log4j2
@Component
@AllArgsConstructor
public class AnnotationBasedValidatorFactory
{
   private final EntityManager entityManager;

   // maps a generic to a generic instance of an AnnotationBasedValidator including the ignoreReferenceUniqueErrors flag
   private final Map<Class<? extends BaseEntity>, Map<Boolean, AnnotationBasedValidator<? extends BaseEntity>>> validatorMap = new HashMap<>();

   /**
    * Returns an generic instance of {@link AnnotationBasedValidator} ignoring reference unique errors,
    * the instance will be created if absent.
    * TODO Ignoring reference unique errors is the standard behaviour since GOM-1545. Why?
    *
    * @param type specific generic
    * @param <T>  specific generic
    * @return generic instance of {@link AnnotationBasedValidator}
    */
   public <T extends BaseEntity> AnnotationBasedValidator<T> create(@NonNull Class<T> type)
   {
      return create(type, true);
   }

   /**
    * Returns an generic instance of {@link AnnotationBasedValidator}, the instance will be created if absent.
    *
    * @param type                        specific generic
    * @param ignoreReferenceUniqueErrors if {@literal true} unique errors for reference fields will raise no error
    * @param <T>                         specific generic
    * @return generic instance of {@link AnnotationBasedValidator}
    */
   @SuppressWarnings("unchecked")
   public <T extends BaseEntity> AnnotationBasedValidator<T> create(@NonNull Class<T> type,
         boolean ignoreReferenceUniqueErrors)
   {
      return (AnnotationBasedValidator<T>) validatorMap
            .computeIfAbsent(type, aClass -> new HashMap<>())
            .computeIfAbsent(ignoreReferenceUniqueErrors, flag ->
                  new AnnotationBasedValidator<>(type, flag, entityManager));
   }
}
