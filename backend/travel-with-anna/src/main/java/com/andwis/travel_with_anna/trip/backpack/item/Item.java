package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Size(max = 60)
    @Column(name = "item", length = 60)
    private String item;

    @Size(max = 40)
    @Column(name = "quantity", length = 40)
    private String quantity;

    @Column(name = "is_packed")
    private boolean isPacked;

    @ManyToOne
    @JoinColumn(name = "backpack_id")
    @JsonIgnore
    private Backpack backpack;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expanse_id")
    private Expanse expanse;

    public void addExpanse(Expanse expanse) {
        this.expanse = expanse;
        expanse.setItem(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item1 = (Item) o;
        return isPacked == item1.isPacked && Objects.equals(itemId, item1.itemId)
                && Objects.equals(item, item1.item) && Objects.equals(quantity, item1.quantity)
                && Objects.equals(backpack, item1.backpack
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, item, quantity, isPacked);
    }
}
