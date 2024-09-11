package com.andwis.travel_with_anna.trip.day.activity.badge;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "badge")
public class Badge{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long badgeId;

    @NotNull
    @Size(max = 40)
    @Column(name = "name", length = 40)
    private String name;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Badge badge = (Badge) o;
        return Objects.equals(badgeId, badge.badgeId) && Objects.equals(name, badge.name) && Objects.equals(activity, badge.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(badgeId, name);
    }
}
