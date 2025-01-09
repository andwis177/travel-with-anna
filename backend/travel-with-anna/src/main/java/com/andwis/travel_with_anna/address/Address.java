package com.andwis.travel_with_anna.address;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {
    protected static final int MAX_NAME_LENGTH = 100;
    protected static final int MAX_CODE_LENGTH = 3;
    protected static final int MAX_PHONE_LENGTH = 30;
    protected static final int MAX_EMAIL_LENGTH = 150;
    protected static final int MAX_CURRENCY_LENGTH = 10;
    protected static final int MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Size(max = MAX_NAME_LENGTH)
    @Column(name = "place", length = MAX_NAME_LENGTH)
    private String place;

    @Size(max = MAX_NAME_LENGTH)
    @Column(name = "country", length = MAX_NAME_LENGTH)
    private String country;

    @Size(max = MAX_CODE_LENGTH)
    @Column(name = "country_code", length = MAX_CODE_LENGTH)
    private String countryCode;

    @Size(max = MAX_NAME_LENGTH)
    @Column(name = "city", length = MAX_NAME_LENGTH)
    private String city;

    @Size(max = MAX_LENGTH)
    @Column(name = "address")
    private String address;

    @Size(max = MAX_LENGTH)
    @Column(name = "website")
    private String website;

    @Size(max = MAX_PHONE_LENGTH)
    @Column(name = "phone", length = MAX_PHONE_LENGTH)
    private String phoneNumber;

    @Size(max = MAX_EMAIL_LENGTH)
    @Column(name = "email", length = MAX_EMAIL_LENGTH)
    private String email;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Activity> activities = new HashSet<>();

    @Size(max = MAX_CURRENCY_LENGTH)
    @Column(name = "currency", length = MAX_CURRENCY_LENGTH)
    private String currency;

    public void addLinkedActivity(Activity activity) {
        this.activities.add(activity);
        activity.setAddress(this);
    }
}