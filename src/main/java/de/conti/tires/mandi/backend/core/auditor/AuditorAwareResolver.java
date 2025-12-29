package de.conti.tires.mandi.backend.core.auditor;

import de.conti.tires.mandi.backend.user.UserEntity;
import de.conti.tires.mandi.container.security.services.UserDetailsImpl;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * Auditor to resolves {@link UserEntity}.
 */
@Log4j2
@Component
@SuppressWarnings("unused")
public class AuditorAwareResolver implements AuditorAware<UserEntity> {
    //    @Autowired
//    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    /**
     * Returns the current auditor as {@link Optional} of {@link UserEntity}.
     *
     * @return {@link Optional} of {@link UserEntity}
     */
    @NonNull
    @Override
    public Optional<UserEntity> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                user = (UserDetailsImpl) principal;
            }
        }
        if (user == null) {
            log.debug("Current auditor unavailable!");
            return Optional.empty();
        }

        //if (user instanceof GomesDetailedUser) {
        //GomesDetailedUser gomesUser = (GomesDetailedUser) user;
        log.trace("Resolving current auditor for login {} with UUID {}", user.getUsername(), user.getUuid());
        Session session = entityManager.unwrap(Session.class);
        //UserEntity entity = session.load(UserEntity.class, user.getUuid());
        //return Optional.of(entity);
        UserEntity entity = entityManager.find(UserEntity.class, user.getUuid());
        return Optional.ofNullable(entity);
        //}

        //log.trace("Resolving current auditor: {}", user.getUsername());

        //return userRepository.findOneByAdLoginIgnoreCase(user.getUsername());
    }
}
