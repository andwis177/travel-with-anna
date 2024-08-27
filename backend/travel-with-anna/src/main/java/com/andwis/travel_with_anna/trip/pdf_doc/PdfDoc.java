package com.andwis.travel_with_anna.trip.pdf_doc;

import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "pdf", columnDefinition = "TEXT")
    private byte[] data;
}
