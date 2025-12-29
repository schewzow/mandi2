package de.conti.tires.mandi.backend.core.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.conti.tires.mandi.backend.core.exception.ValidationException;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;


/**
 * Encapsulates global and field specific validation errors.
 * <p>
 * Provides some builder functions like {@link #addGlobalError(String, Serializable...)} and
 * {@link #setFieldError(String, String, Serializable...)}.
 */
public class ValidationErrors implements Serializable
{
   private static final long serialVersionUID = 1L;

   /**
    * Message key for unique constraint violation.
    */
   public static final String UNIQUE_ERROR = "error.validation.unique";

   /**
    * Registered global errors.
    */
   @Getter
   private Set<ValidationError> global = new HashSet<>();
   /**
    * Registered field errors per field.
    */
   @Getter
   private HashMap<String, Set<ValidationError>> fields = new HashMap<>();

   /**
    * Adds a global error.
    *
    * @param error error to add (equal ones will be joined)
    */
   public void addGlobalError(@NonNull ValidationError error)
   {
      global.add(error);
   }

   /**
    * Adds a field related error.
    *
    * @param field field name
    * @param error error to add (equal ones will be joined)
    */
   public void addFieldError(@NonNull String field, @NonNull ValidationError error)
   {
      Set<ValidationError> errors = fields.computeIfAbsent(field, k -> new HashSet<>());
      errors.add(error);
   }

   /**
    * Adds a global error.
    *
    * @param code       error message key
    * @param parameters optional message parameters
    */
   public void addGlobalError(@NonNull String code, Serializable... parameters)
   {
      addGlobalError(new ValidationError(code, parameters));
   }

   /**
    * Adds multiple global errors.
    *
    * @param errors Errors to add.
    */
   public void addGlobalErrors(Collection<ValidationError> errors)
   {
      this.global.addAll(errors);
   }

   /**
    * Adds a field error.
    * <p>
    * In general you will prefer using  {@link #setFieldError(String, String, Serializable...)} because multiple error
    * messages usually do not make sense for the same field.
    *
    * @param field      field name
    * @param code       error message key
    * @param parameters optional message parameters
    */
   public void addFieldError(@NonNull String field, @NonNull String code, Serializable... parameters)
   {
      addFieldError(field, new ValidationError(code, parameters));
   }

   /**
    * Same as {@link #addFieldError(String, String, Serializable...)} but calls {@link #clearField(String)} first.
    * <p>
    * This will force the error to be the only one for the field.
    *
    * @param field      field name
    * @param code       error message key
    * @param parameters optional message parameters
    */
   public void setFieldError(@NonNull String field, @NonNull String code, Serializable... parameters)
   {
      clearField(field);
      addFieldError(field, code, parameters);
   }

   /**
    * Helper method for adding 'required' validation errors if necessary.
    *
    * @param field field name
    * @param value value to test via {@link StringUtils#isBlank(CharSequence)}
    * @return {@code true} if a validation error is present
    */
   public boolean decideRequiredError(@NonNull String field, String value)
   {
      if (StringUtils.isBlank(value))
      {
         addFieldError(field, "error.validation.required");
         return true;
      }
      return false;
   }

   /**
    * Helper method for adding 'required' validation errors if necessary.
    *
    * @param field field name
    * @param value value to test for {@code null}
    * @return {@code true} if a validation error is present
    */
   public boolean decideRequiredError(@NonNull String field, Object value)
   {
      if (value == null)
      {
         addFieldError(field, "error.validation.required");
         return true;
      }
      return false;
   }

   /**
    * Helper method for adding 'maxLength' validation errors if necessary.
    * If the given value is not {@code null} it must not exceed the given length.
    *
    * @param field     Field name.
    * @param value     Value to test for length.
    * @param maxLength Maximal allowed length.
    * @return {@code true} if a validation error is present.
    */
   public boolean decideLengthError(@NonNull String field, String value, int maxLength)
   {
      if (value == null)
      {
         return false;
      }

      if (value.length() > maxLength)
      {
         addFieldError(field, "error.validation.maxLength", maxLength);
         return true;
      }
      return false;
   }

   /**
    * Helper method for adding 'bound' (min/max) validation errors if necessary.
    * If the given value is not {@code null} it must be within the specified range.
    * Rules are not checked: if no boundary is defined no test will fail.
    * If one bound is defined only, you define greater or smaller test actually.
    *
    * @param field        field name.
    * @param value        value to test for length.
    * @param min          minimal value
    * @param minInclusive minimal value is included in valid range
    * @param max          maximal value
    * @param maxInclusive maximal value is included in valid range
    * @return {@code true} if a validation error is present.
    */
   public boolean decideRangeError(@NonNull String field, Number value, Number min, boolean minInclusive,
         Number max, boolean maxInclusive)
   {
      if (value == null)
      {
         return false;
      }

      double valD = value.doubleValue();

      if (min != null && max != null)
      {
         return rangeCheck(field, valD, min, minInclusive, max, maxInclusive);
      }
      else if (min != null)
      {
         return minBoundaryCheck(field, valD, min, minInclusive);
      }
      else if (max != null)
      {
         return maxBoundaryCheck(field, valD, max, maxInclusive);
      }
      return false;
   }

   private boolean rangeCheck(@NonNull String field, double valD, Number min, boolean minInclusive,
         Number max, boolean maxInclusive)
   {
      if (min != null && max != null)
      {
         double minD = min.doubleValue();
         double maxD = max.doubleValue();

         boolean minViolation = ((minInclusive && valD < minD) || (!(minInclusive) && valD <= minD));
         boolean maxViolation = ((maxInclusive && valD > maxD) || (!(maxInclusive) && valD >= maxD));

         if (minViolation || maxViolation)
         {
            addFieldError(field, "error.validation.notInRange",
                  minInclusive ? "[" : "(",
                  min,
                  max,
                  maxInclusive ? "]" : ")");
            return true;
         }
      }
      return false;
   }

   private boolean minBoundaryCheck(@NonNull String field, double valD, Number min, boolean minInclusive)
   {
      double minD = min.doubleValue();
      if ((minInclusive && valD < minD) || (!(minInclusive) && valD <= minD))
      {
         addFieldError(field, "error.validation.notInSingleBoundary", minInclusive ? ">=" : ">", min);
         return true;
      }
      return false;
   }

   private boolean maxBoundaryCheck(@NonNull String field, double valD, Number max, boolean maxInclusive)
   {
      double maxD = max.doubleValue();
      if ((maxInclusive && valD > maxD) || (!(maxInclusive) && valD >= maxD))
      {
         addFieldError(field, "error.validation.notInSingleBoundary", maxInclusive ? "<=" : "<", max);
         return true;
      }
      return false;
   }

   /**
    * Helper method for adding 'unique constraint violation' validation errors.
    *
    * @param field field name
    * @throws UnsupportedOperationException I18N bean is not available
    */
   public void addUniqueError(@NonNull String field) throws UnsupportedOperationException
   {
      addFieldError(field, UNIQUE_ERROR);
   }

   /**
    * @return {@code true} if no global error and no field was registered (means: no validation error)
    */
   @JsonIgnore
   public boolean isEmpty()
   {
      return global.isEmpty() && fields.isEmpty();
   }

   /**
    * Removes all errors for the given field.
    *
    * @param field field name
    */
   public void clearField(String field)
   {
      fields.remove(field);
   }

   /**
    * Removes all registered errors.
    */
   public void clear()
   {
      global.clear();
      fields.clear();
   }

   /**
    * Throws a {@link ValidationException} from this object if an error is registered.
    *
    * @throws ValidationException thrown in case of an error
    */
   public void throwExceptionOnError() throws ValidationException
   {
      if (!(isEmpty()))
      {
         throw new ValidationException(this);
      }
   }

   /**
    * Wrapper
    */
   @Value
   public class FieldError
   {
      private String property;
      private List<ValidationError> errors;
   }


   @RequiredArgsConstructor
   @EqualsAndHashCode
   private class ErrorId
   {
      /**
       * property name or {@code null} for global
       */
      private final String field;
      @NonNull
      private final String key;
   }
}
