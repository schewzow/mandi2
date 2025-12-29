package de.conti.tires.mandi.backend.util;

import de.conti.tires.mandi.backend.core.base.BaseEntity;
import jakarta.persistence.Query;
import lombok.NonNull;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * Various Hibernate / JPA related utility functions.
 */
public class HibernateUtils
{
   /**
    * Hint name for specifying the query fetch size (rows fetched at once while getting a {@link Query} result).
    * The standard value is 50.
    */
   public static final String FETCH_SIZE = "org.hibernate.fetchSize";

   /**
    * Checks if the given entity's class is a Hibernate proxy class and resolves the original one in that case.
    *
    * @param entity entity to resolve class for
    * @param <E>    entity type
    * @return original entity class
    */
   @SuppressWarnings("unchecked")
   public static <E> Class<E> unproxyClass(@NonNull E entity)
   {
      if (entity instanceof HibernateProxy)
      {
         HibernateProxy proxy = (HibernateProxy) entity;
         return (Class<E>) proxy.getHibernateLazyInitializer().getPersistentClass();
      }
      else
      {
         return (Class<E>) entity.getClass();
      }
   }

   /**
    * Checks if the given entity is a Hibernate proxy class and resolved the original entity in that case.
    * Requires an active transaction.
    *
    * @param entity entity to resolve
    * @param <E>    entity type
    * @return original entity
    */
   @SuppressWarnings("unchecked")
   public static <E> E unproxyEntity(@NonNull E entity)
   {
      if (entity instanceof HibernateProxy)
      {
         HibernateProxy proxy = (HibernateProxy) entity;
         LazyInitializer initializer = proxy.getHibernateLazyInitializer();
         return (E) initializer.getImplementation();
      }
      else
      {
         return entity;
      }
   }

   /**
    * Replaces the given field with the resolved entity (using {@link #unproxyEntity(Object)}) if it is a Hibernate
    * proxy.
    *
    * @param entity parent object
    * @param getter field getter
    * @param setter field setter
    * @param <E>    parent object type
    * @param <F>    field type
    */
   public static <E, F> void replaceProxy(@NonNull E entity, @NonNull Function<E, F> getter,
         @NonNull BiConsumer<E, F> setter)
   {
      Optional.ofNullable(getter.apply(entity)).ifPresent(value -> setter.accept(entity, unproxyEntity(value)));
   }

   /**
    * Checks if the given entity is a Hibernate proxy class and extracts the UUID from the proxy.
    * Does not requires an active transaction!
    *
    * @param entity entity to resolve.
    * @return the identifier.
    */
   public static UUID getId(@NonNull BaseEntity entity)
   {
      if (entity instanceof HibernateProxy)
      {
         HibernateProxy proxy = (HibernateProxy) entity;
         return (UUID) proxy.getHibernateLazyInitializer().getIdentifier();
      }
      else
      {
         return entity.getUuid();
      }
   }

   /**
    * Sets the fetch size (see {@link #FETCH_SIZE} for more information).
    *
    * @param query     query to set fetch size for
    * @param fetchSize size to set
    */
   public static void setFetchSize(@NonNull Query query, int fetchSize)
   {
      query.setHint(FETCH_SIZE, fetchSize);
   }
}
