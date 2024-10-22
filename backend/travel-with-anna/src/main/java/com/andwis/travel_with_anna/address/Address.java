package com.andwis.travel_with_anna.address;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Size(max = 100)
    @Column(name = "place", length = 100)
    private String place;

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Size(max = 3)
    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "website")
    private String website;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "address")
    private List<Activity> activities;

    @Size(max = 10)
    @Column(name ="currency", length = 10)
    private String currency;

    public void addActivity(Activity activity) {
        if (this.activities == null) {
            this.activities = new ArrayList<>();
        }
        activities.add(activity);
        activity.setAddress(this);
    }
}
