package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "items")
public class Item extends BaseEntity {
    @NotNull
    @Size(max = 60)
    @Column(name = "item", length = 60)
    private String item;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "is_packed")
    private boolean isPacked = false;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expanse_id")
    private Expanse expanse;

    @ManyToOne
    @JoinColumn(name = "backpack_id")
    private Backpack backpack;
}
