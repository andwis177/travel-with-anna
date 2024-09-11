package com.andwis.travel_with_anna.trip.pdf_doc;

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
@Table(name = "pdf")
public class Pdf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pdf_id")
    private Long pdfId;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Lob
    @Column(name = "pdf", columnDefinition = "TEXT")
    private byte[] data;

    @OneToOne(mappedBy = "pdf")
    @JsonIgnore
    private Expanse expanse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pdf pdf = (Pdf) o;
        return Objects.equals(pdfId, pdf.pdfId) && Objects.equals(name, pdf.name) && Objects.deepEquals(data, pdf.data) && Objects.equals(expanse, pdf.expanse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pdfId, name);
    }
}
