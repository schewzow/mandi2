package de.conti.tires.mandi.backend.laboratory.projection;

import de.conti.tires.mandi.backend.core.base.AuditBaseDTO;
import de.conti.tires.mandi.backend.user.projection.UserDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class LaboratoryDetails extends AuditBaseDTO {
    private String name;
    private String shortName;
    private double resultValue;
    private UserDetails labUser;
    private LocalDateTime labDate;
    private boolean labSwitchOn;
    private boolean labSwitchOff;
}
