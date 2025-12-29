package de.conti.tires.mandi.backend.core.base;

import de.conti.tires.mandi.backend.user.projection.UserSummary;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Same as {@link AuditBaseEntity} but without JPA context, version and deleted.
 */
@Data
@NoArgsConstructor
public class AuditBaseDTO
{
   protected UUID uuid;
   protected boolean active;
   protected LocalDateTime createdDate;
   protected UserSummary createdBy;
   protected LocalDateTime lastModifiedDate;
   protected UserSummary lastModifiedBy;

   /**
    * Creates an instance from an entity.
    *
    * @param source entity
    */
   public AuditBaseDTO(@NonNull AuditBaseEntity source)
   {
      uuid = source.getUuid();
      active = source.isActive();
      createdDate = source.createdDate;
      createdBy = UserSummary.of(source.getCreatedBy());
      lastModifiedDate = source.lastModifiedDate;
      lastModifiedBy = UserSummary.of(source.getLastModifiedBy());
   }
}
