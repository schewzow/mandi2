package de.conti.tires.mandi.backend.core.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;


/**
 * Spring Data JPA Repository for index relevant {@link BaseEntity} and all its subclasses.
 *
 * @param <T> subclass of {@link BaseEntity}
 */
@NoRepositoryBean
public interface BaseEntityRepository<T extends UuidIdentifiable> extends JpaRepository<T, UUID> {

    // generic filter implementation
    @Query(
            "SELECT A FROM #{#entityName} A "
    )
    Page<T> findFiltered(
            String filter,
            Pageable pageable
    );
}
