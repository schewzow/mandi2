package de.conti.tires.mandi.backend.user;

import de.conti.tires.mandi.backend.core.base.BaseEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseEntityRepository<UserEntity> {

    Optional<UserEntity> findByUserName(String username);

    Boolean existsByUserName(String username);

    //Boolean existsByEmail(String email);
}
