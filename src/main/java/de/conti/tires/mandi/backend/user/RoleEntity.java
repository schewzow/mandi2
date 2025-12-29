package de.conti.tires.mandi.backend.user;

import de.conti.tires.mandi.backend.core.base.AuditBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "roles")
public class RoleEntity extends AuditBaseEntity implements Serializable {

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name")
    private AppRole roleName;

    public RoleEntity(AppRole roleName) {
        this.roleName = roleName;
    }
}
