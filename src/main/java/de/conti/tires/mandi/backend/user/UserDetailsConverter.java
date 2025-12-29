package de.conti.tires.mandi.backend.user;

import de.conti.tires.mandi.backend.core.exception.ApiException;
import de.conti.tires.mandi.backend.user.projection.UserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;


/**
 * Converter from entity to details projection.
 */
@Component
@RequiredArgsConstructor
public class UserDetailsConverter implements Converter<UserEntity, UserDetails>
{
   private UserSummaryConverter summaryConverter = new UserSummaryConverter();

   /**
    * Convert the source object of type {@code S} to target type {@code T}.
    *
    * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
    * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
    * @throws IllegalArgumentException if the source cannot be converted to the desired target type
    */
   @Override
   public UserDetails convert(UserEntity source)
   {
      ModelMapper mapper = new ModelMapper();

      UserDetails details = new UserDetails();
      mapper.map(source, details);

      Optional.ofNullable(source.getCreatedBy())
            .ifPresent(user -> details.setCreatedBy(summaryConverter.convert(user)));
      Optional.ofNullable(source.getLastModifiedBy())
            .ifPresent(user -> details.setLastModifiedBy(summaryConverter.convert(user)));

      return details;
   }

   private <T> T buildDTO(Object entity, Class<T> type, ModelMapper mapper)
   {
      if (entity == null)
      {
         return null;
      }

      try
      {
         T dto = type.getConstructor().newInstance();
         mapper.map(entity, dto);
         return dto;
      }
      catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
      {
         throw new ApiException(e);
      }
   }
}
