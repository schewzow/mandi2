package de.conti.tires.mandi.backend.core.base;

import de.conti.tires.mandi.backend.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


/**
 * Audit Base Entity with extended meta information.
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditBaseEntity extends BaseEntity
{
   @Getter
   @CreatedDate
   @Column(updatable = false)
   @ColumnDefault("CURRENT_TIMESTAMP")
   protected LocalDateTime createdDate;

   @Getter
   @CreatedBy
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(updatable = false)
   protected UserEntity createdBy;

   @Getter
   @LastModifiedDate
   @ColumnDefault("CURRENT_TIMESTAMP")
   protected LocalDateTime lastModifiedDate;

   @Getter
   @LastModifiedBy
   @ManyToOne(fetch = FetchType.LAZY)
   protected UserEntity lastModifiedBy;
}
