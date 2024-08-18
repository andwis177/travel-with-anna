package com.andwis.travel_with_anna.role;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    private final static String USER_ROLE = "USER";
    private final static String ADMIN_ROLE = "ADMIN";
    private final static String ADMIN_AUTHORITY = "Admin";
    private final static String USER_AUTHORITY = "User";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @NotNull
    @Column(unique = true)
    private String roleName;

    @NotNull
    @Column()
    private String authority;

    public static String getUserRole() {
        return USER_ROLE;
    }

    public static String getAdminRole() {
        return ADMIN_ROLE;
    }

    public static String getAdminAuthority() {
        return ADMIN_AUTHORITY;
    }

    public static String getUserAuthority() {
        return USER_AUTHORITY;
    }
}
