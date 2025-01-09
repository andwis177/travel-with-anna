package com.andwis.travel_with_anna.role;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
public class Role {

    public static final int ROLE_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Size(max = ROLE_LENGTH)
    @NotNull
    @Column(name = "role_name", unique = true, length = ROLE_LENGTH)
    private String roleName;

    @Size(max = ROLE_LENGTH)
    @NotNull
    @Column(name = "authority", unique = true, length = ROLE_LENGTH)
    private String roleAuthority;
}
