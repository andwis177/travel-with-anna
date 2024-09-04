package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "backpack")
public class Backpack extends BaseEntity {

    @OneToMany(mappedBy = "backpack", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Item> items = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @OneToOne(mappedBy = "backpack")
    private Trip trip;

    public void setNote(Note note) {
        this.note = note;
        note.setBackpack(this);
    }
}
