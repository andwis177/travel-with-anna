package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.trip.pdf_doc.PdfDoc;
import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "expanses")
public class Expanse extends BaseEntity {
    @Column(name = "expanse_name")
    private String expanseName;

    @NotNull
    @Column(name = "currency", length = 10)
    private String currency;

    @NotNull
    @Column(name = "price")
    private BigDecimal price;

    @NotNull
    @Column(name = "paid")
    private BigDecimal paid;

    @NotNull
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pdf_doc_id")
    private PdfDoc pdfDoc;
}
