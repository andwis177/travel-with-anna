package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.security.OwnableByUser;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "backpack")
public class Backpack implements OwnableByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backpack_id")
    private Long backpackId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "backpack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @OneToOne(mappedBy = "backpack")
    private Trip trip;

    public void addItem(Item item) {
        items.add(item);
        item.setBackpack(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.setBackpack(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Backpack backpack = (Backpack) o;
        return Objects.equals(backpackId, backpack.backpackId)
                && Objects.equals(items, backpack.items)
                && Objects.equals(trip, backpack.trip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backpackId);
    }

    @Override
    public User getOwner() {
        return this.trip.getOwner();
    }
}
