package de.conti.tires.mandi.backend.user;

import de.conti.tires.mandi.backend.user.projection.UserSummary;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;


/**
 * Converter for {@link UserEntity} to {@link UserSummary}.
 */
public class UserSummaryConverter implements Converter<UserEntity, UserSummary>
{
   private final ModelMapper modelMapper = new ModelMapper();

   /**
    * Convert the source object of type {@code S} to target type {@code T}.
    *
    * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
    * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
    * @throws IllegalArgumentException if the source cannot be converted to the desired target type
    */
   @Override
   public UserSummary convert(@NonNull UserEntity source)
   {
      UserSummary summary = new UserSummary();
      modelMapper.map(source, summary);
      return summary;
   }
}
