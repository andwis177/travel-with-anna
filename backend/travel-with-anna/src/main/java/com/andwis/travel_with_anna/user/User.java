package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.trip.trip.Trip;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Size(max = 30)
    @Column(unique = true, length = 30)
    private String userName;

    @Column(unique = true)
    private String email;

    private String password;

    private boolean accountLocked;

    private boolean enabled;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @NotNull
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @NotNull
    @Column(name = "avatar_id")
    private Long avatarId;

    @OneToMany(fetch = EAGER, mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trip> ownedTrips;

    @Override
    public String getName() {
        return this.getEmail();
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }

    public void addTrip(Trip trip) {
        this.ownedTrips.add(trip);
        trip.setOwner(this);
    }

    public void removeTrip(Trip trip) {
        this.ownedTrips.remove(trip);
        trip.setOwner(null);
    }
}
