package de.conti.tires.mandi.backend.laboratory;

import de.conti.tires.mandi.backend.core.base.BaseEntityRepository;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


/**
 * Repository for {@link LaboratoryEntity}.
 */
@Repository
//@RepositoryRestResource(exported = false, collectionResourceRel = "content")
public interface LaboratoryRepository extends BaseEntityRepository<LaboratoryEntity> {

    Boolean existsByName(String name);
    Optional<LaboratoryEntity> findByName(String name);

    /**
     * Finds a unique name violating record.
     *
     * @param name name to check
     * @param uuid UUID of the record to check uniqueness for
     * @return candidates
     */
    // @QueryHints is important, otherwise an exception can be thrown if any of the entity's fields has an error, like > max length
    @QueryHints(value = { @QueryHint(name = "hibernate.flushMode", value = "COMMIT") })
    @Query("select e from laboratories e where e.name = :name and e.uuid <> :uuid")
    //@Query(value = "SELECT * FROM laboratories WHERE name = :name AND uuid <> :uuid", nativeQuery = true)
    Optional<LaboratoryEntity> findUniqueNameViolation(@Param("name") String name, @Param("uuid") UUID uuid);

    @Override
    @Query(
            "SELECT A FROM laboratories A " +
                    "WHERE " +
                    "CONCAT(" +
                    "CASE WHEN A.name is NULL then '' ELSE UPPER(A.name) END, ' ', " +
                    "CASE WHEN A.shortName is NULL then '' ELSE UPPER(A.shortName) END) " +
                    "LIKE %:filter%"
    )
    Page<LaboratoryEntity> findFiltered(
            String filter,
            Pageable pageable
    );
}
