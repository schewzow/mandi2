package de.conti.tires.mandi.backend.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "refreshtoken")
@Getter
@Setter
public class RefreshTokenEntity {
    @Id
    @Column(length = 36, columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR) // -> new hibernate version
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID uuid;

    @OneToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    private UserEntity user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
