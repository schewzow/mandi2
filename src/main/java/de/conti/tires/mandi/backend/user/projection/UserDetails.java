package de.conti.tires.mandi.backend.user.projection;

import de.conti.tires.mandi.backend.core.base.AuditBaseDTO;
import de.conti.tires.mandi.backend.user.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Details view read transfer object of a {@link UserEntity}.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetails extends AuditBaseDTO {
    private String userName;
    private String firstname;
    private String lastname;
    private String email;
}
