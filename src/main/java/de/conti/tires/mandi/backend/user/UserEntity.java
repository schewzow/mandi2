package de.conti.tires.mandi.backend.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.conti.tires.mandi.backend.core.base.AuditBaseEntity;
import de.conti.tires.mandi.backend.laboratory.LaboratoryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        })
public class UserEntity extends AuditBaseEntity implements Serializable {

    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String userName;

//    @NotBlank
//    @Size(max = 50)
//    @Email
//    @Column(name = "email")
//    private String email;

    @NotBlank
    @JsonIgnore
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @Column(length = 64)
    private String firstname;

    @Column(length = 64)
    private String lastname;

    /**
     * Users email addresses as CSV string.
     * <p>
     * This field should only be altered by an administrator
     */
    @Column(length = 1024)
    private String email;

    public UserEntity(String userName, String password, String firstname, String lastname, String email) {
        this.userName = userName;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    /**
     * Users preferred language as ISO code.
     * Must be compatible to {@link Locale} language tag.
     */
    @Column(columnDefinition = "VARCHAR2(5) default 'en-US' not null")
    private String language = LocaleContextHolder.getLocale().toLanguageTag();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(name = "userroles",
            joinColumns = @JoinColumn(name = "user_uuid"),
            inverseJoinColumns = @JoinColumn(name = "role_uuid"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    //@JoinTable(indexes = { @Index(name = "FK_USERS_LABORATORIES", columnList = "LABORATORIES_UUID") })
    @JoinTable(name = "userlaboratories",
            joinColumns = @JoinColumn(name = "user_uuid"),
            inverseJoinColumns = @JoinColumn(name = "laboratory_uuid"))
    private Set<LaboratoryEntity> laboratories = new HashSet<>();

    /**
     * Default constructor setting {@link #active} to {@code false}.
     */
    public UserEntity()
    {
        active = false;
    }
}
