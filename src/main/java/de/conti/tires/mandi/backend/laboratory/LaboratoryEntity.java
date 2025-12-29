package de.conti.tires.mandi.backend.laboratory;

import de.conti.tires.mandi.backend.core.base.AuditBaseEntity;
import de.conti.tires.mandi.backend.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "laboratories")
@Getter
@Setter
public class LaboratoryEntity extends AuditBaseEntity implements Serializable {

    @Column(length = 64, unique = true)
    @NotNull
    private String name;

    @Column(length = 10)
    private String shortName;

    @Column(length = 10)
    private double resultValue;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity labUser;

    @Column
    private LocalDateTime labDate;

    @Column
    private boolean labSwitchOn;

    @Column
    private boolean labSwitchOff;
}
