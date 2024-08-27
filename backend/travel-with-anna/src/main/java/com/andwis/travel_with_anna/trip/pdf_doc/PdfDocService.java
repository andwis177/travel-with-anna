package com.andwis.travel_with_anna.trip.pdf_doc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfDocService {
    private final PdfDocRepository pdfDocRepository;

    public void savePdfDoc(PdfDoc pdfDoc) {
        pdfDocRepository.save(pdfDoc);
    }
}
