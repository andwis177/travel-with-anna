package com.andwis.travel_with_anna.trip.day.activity.type.accommodation;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "accommodations")
public class Accommodation extends Activity {
    @Size(max = 40)
    @NotNull
    @Column(name = "accommodation_type", length = 40)
    private String accommodationType;

}
