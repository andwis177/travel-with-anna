package com.andwis.travel_with_anna.trip.pdf_doc;

import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "pdf_doc")
public class PdfDoc extends BaseEntity {

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Lob
    @Column(name = "pdf", columnDefinition = "TEXT")
    private byte[] data;

    @OneToOne(mappedBy = "pdfDoc")
    private Expanse expanse;
}
