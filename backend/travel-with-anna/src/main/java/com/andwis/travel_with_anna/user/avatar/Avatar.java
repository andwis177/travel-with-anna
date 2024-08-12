package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
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
@Table(name = "avatars")
@EntityListeners(AuditingEntityListener.class)
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatar_id")
    private Long avatarId;
    @Column(name = "avatar")
    private String avatar;
    @OneToOne(mappedBy = "avatar")
    private User user;
}
