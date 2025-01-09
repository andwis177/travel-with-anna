package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "backpack")
public class Backpack implements OwnByUser {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backpack_id")
    private Long backpackId;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "backpack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> backpackItems = new ArrayList<>();

    @OneToOne(mappedBy = "backpack")
    private Trip trip;

    public void addItem(Item item) {
        this.backpackItems.add(item);
        item.setBackpack(this);
    }

    public void removeItem(Item item) {
        this.backpackItems.remove(item);
        item.setBackpack(null);
    }

    public List<Item> getBackpackItems() {
        return List.copyOf(this.backpackItems);
    }

    @Override
    public User getOwner() {
        return Objects.requireNonNull(this.trip, "Backpack must be linked to a trip to have an owner")
                .getOwner();
    }
}
