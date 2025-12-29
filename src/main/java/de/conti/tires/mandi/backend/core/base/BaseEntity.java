package de.conti.tires.mandi.backend.core.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


/**
 * Base Entity Class
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements UuidIdentifiable, Serializable
{
   /**
    * Entities unique identifier.
    */
   @Id
   @Column(length = 36, columnDefinition = "char(36)")
   @JdbcTypeCode(SqlTypes.CHAR) // -> new hibernate version
   @GeneratedValue(strategy = GenerationType.UUID)
   protected UUID uuid;

   /**
    * Record version for optimistic locking.
    */
   @Version
   protected Long version;

   /**
    * Entities activity flag.
    * {@code true} by default.
    */
   protected boolean active = true;

   /**
    * Entities activity flag.
    * {@code true} by default.
    */
   protected boolean deleted = false;

   @Override
   public String toString()
   {
      return this.getClass().getSimpleName() + (uuid == null ? ": not persisted" : (": " + uuid));
   }

   /**
    * Tests if this object has the same UUID as the specified one.
    *
    * @param object object to test, {@literal null}-safe
    * @return {@literal true} if the specified object is not {@literal null} and its UUID matches this object's one
    * (or both are {@literal null})
    */
   public boolean equalsByUuid(UuidIdentifiable object)
   {
      return object != null && Objects.equals(uuid, object.getUuid());
   }
}
