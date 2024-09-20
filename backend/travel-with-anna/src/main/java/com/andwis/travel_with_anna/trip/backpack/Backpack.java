package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "backpack")
public class Backpack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "backpack_id")
    private Long backpackId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "backpack", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Item> items;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @OneToOne(mappedBy = "backpack")
    @JsonIgnore
    private Trip trip;

    public void setNote(Note note) {
        this.note = note;
        note.setBackpack(this);
    }

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
        return Objects.equals(backpackId, backpack.backpackId) && Objects.equals(items, backpack.items) && Objects.equals(note, backpack.note) && Objects.equals(trip, backpack.trip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backpackId);
    }
}
