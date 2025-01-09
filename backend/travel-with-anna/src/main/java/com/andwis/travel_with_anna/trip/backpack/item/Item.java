package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "items")
public class Item {

    public static final int ITEM_NAME_MAX_LENGTH = 100;
    public static final int QUANTITY_MAX_LENGTH = 70;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @EqualsAndHashCode.Include
    @Size(max = ITEM_NAME_MAX_LENGTH)
    @Column(name = "item_name", length = ITEM_NAME_MAX_LENGTH)
    private String itemName;

    @EqualsAndHashCode.Include
    @Size(max = QUANTITY_MAX_LENGTH)
    @Column(name = "quantity", length = QUANTITY_MAX_LENGTH)
    private String quantity;

    @Column(name = "is_packed")
    private boolean isPacked;

    @ManyToOne
    @JoinColumn(name = "backpack_id")
    private Backpack backpack;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expanse_id")
    private Expanse expanse;

    public void addExpanse(@NotNull Expanse expanse) {
        this.expanse = expanse;
        expanse.setItem(this);
    }
}
