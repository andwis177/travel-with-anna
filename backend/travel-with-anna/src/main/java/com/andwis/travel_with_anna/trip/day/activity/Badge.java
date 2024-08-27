package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "badge")
public class Badge extends BaseEntity {
    @NotNull
    @Size(max = 40)
    @Column(name = "name", length = 40)
    private String name;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activity;
}
