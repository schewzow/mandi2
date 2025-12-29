package de.conti.tires.mandi.backend.user.projection;


import de.conti.tires.mandi.backend.user.UserEntity;

import java.util.UUID;


/**
 * Summary information of a {@link UserEntity}.
 */
public interface UserSummaryInfo
{
   /**
    *
    * @return primary key
    */
   UUID getUuid();

   /**
    *
    * @return user's first name
    */
   String getFirstname();

   /**
    *
    * @return user's last name
    */
   String getLastname();

   /**
    *
    * @return user's email address
    */
   String getEmail();
}
