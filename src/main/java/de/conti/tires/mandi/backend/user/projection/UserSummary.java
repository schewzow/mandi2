package de.conti.tires.mandi.backend.user.projection;

import de.conti.tires.mandi.backend.user.UserEntity;
import de.conti.tires.mandi.backend.util.StringUtils;
import lombok.*;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;


/**
 * User summary information
 */
@EqualsAndHashCode(of = "uuid")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary implements UserSummaryInfo
{
   /**
    * primary key
    */
   private UUID uuid;

   /**
    * Users first name
    */
   private String firstname;

   /**
    * Users last name
    */
   private String lastname;

   /**
    * Users email
    */
   private String email;

   /**
    * Creates a summary of an entity - returning {@code null} for {@code null}.
    *
    * @param user entity
    * @return resulting DTO
    */
   public static UserSummary of(UserEntity user)
   {
      return user == null ? null : new UserSummary(user.getUuid(), user.getFirstname(), user.getLastname(), user.getEmail());
   }

   /**
    * Creates a readable name (String) for a given {@link UserEntity}.
    *
    * @param userEntity user that should be generated a readable name from.
    * @return the name of the user contained in the {@link UserEntity} as a readable String.
    */
   public static String buildReadableName(final @NonNull UserEntity userEntity)
   {
      final String readableName = StringUtils.joinNonNulls(" ", userEntity.getFirstname(),
            userEntity.getLastname());
      if (isEmpty(readableName))
      {
         return userEntity.getUserName();
      }
      return readableName;
   }

}
