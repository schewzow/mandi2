package de.conti.tires.mandi.backend.core.base;

import java.util.UUID;


/**
 * Interface that ensures getter for {@link UUID}.
 */
public interface UuidIdentifiable
{
   /**
    * Returns a {@link UUID}.
    *
    * @return {@link UUID}
    */
   UUID getUuid();
}
