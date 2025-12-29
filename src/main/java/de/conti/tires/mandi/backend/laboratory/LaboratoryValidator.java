package de.conti.tires.mandi.backend.laboratory;

import de.conti.tires.mandi.backend.core.validation.AnnotationBasedValidatorFactory;
import de.conti.tires.mandi.backend.core.validation.ValidationErrors;
import de.conti.tires.mandi.backend.core.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Validator for laboratories.
 * <p>
 * Name must be set and must be unique.
 * <p>
 * Location must be set.
 * <p>
 * Email must be (optional) sequence of comma separated valid email addresses.
 * <p>
 * Mixing lab flag cannot be removed if used as mixing lab.
 */
@Component
@RequiredArgsConstructor
public class LaboratoryValidator implements Validator<LaboratoryEntity>
{
   private static final String EMAIL_REGEX = "[^\\s@]+@[^\\s@]+\\.[^\\s@]+";
   private static final String EMAILS_REGEX = "\\s*" + EMAIL_REGEX + "\\s*(" + "\\s*,\\s*" + EMAIL_REGEX + "\\s*)*";

   private final LaboratoryRepository laboratoryRepository;
   private final AnnotationBasedValidatorFactory annotationBasedValidatorFactory;

   @Override
   public void validate(LaboratoryEntity entity, LaboratoryEntity previousState,
         Map<String, Object> data, ValidationErrors errors)
   {
      annotationBasedValidatorFactory.create(LaboratoryEntity.class).validate(entity, previousState, data, errors);

//      Optional.ofNullable(entity.getEmail()).ifPresent(email ->
//      {
//         if (!email.matches(EMAILS_REGEX))
//         {
//            errors.addFieldError("email", "error.validation.emails");
//         }
//      });

      if (data.containsKey("name"))
      {
         laboratoryRepository.findUniqueNameViolation(entity.getName(), entity.getUuid())
               .ifPresent(lab -> errors.addUniqueError("name"));
      }
   }
}
