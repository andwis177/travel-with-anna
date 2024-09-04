package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "notes")
public class Note extends BaseEntity {
    @Size(max = 500)
    @Column(length = 500)
    private String note;

    @OneToOne(mappedBy = "note")
    private Day day;

    @OneToOne(mappedBy = "note")
    private Backpack backpack;

    @OneToOne(mappedBy = "note")
    private Activity activity;
}
