package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.trip.Trip;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements Principal, OwnByUser {

    protected static final int MAX_LENGTH = 255;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String PASSWORD_VALIDATION_MESSAGE =
            "Password should be at least " + PASSWORD_MIN_LENGTH + " characters long and not more the " + MAX_LENGTH + "characters long.";
    protected static final int NAME_MAX_LENGTH = 30;


    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @EqualsAndHashCode.Include
    @Size(max = NAME_MAX_LENGTH)
    @Column(name = "user_name", unique = true, length = NAME_MAX_LENGTH)
    private String userName;

    @EqualsAndHashCode.Include
    @Size(max = MAX_LENGTH)
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = MAX_LENGTH, message = PASSWORD_VALIDATION_MESSAGE)
    private String password;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Column(name = "enabled")
    private boolean enabled;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date", insertable = false)
    private LocalDateTime lastModifiedDate;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "avatar_id")
    private Long avatarId;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trip> trips = new HashSet<>();

    @Override
    public String getName() {
        return this.getEmail();
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }

    public void addTrip(Trip trip) {
        this.trips.add(trip);
        trip.setOwner(this);
    }

    public void removeTrip(Trip trip) {
        this.trips.remove(trip);
        trip.setOwner(null);
    }

    @Override
    public User getOwner() {
        return this;
    }
}
