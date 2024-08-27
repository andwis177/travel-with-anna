package com.andwis.travel_with_anna.trip.pdf_doc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("pdf_doc")
@Tag(name = "PdfDoc")
public class PdfDocController {
    private final PdfDocService service;

    @PostMapping("/create")
    public ResponseEntity<Void> savePdfDoc(PdfDoc pdfDoc) {
        service.savePdfDoc(pdfDoc);
        return ResponseEntity.accepted().build();
    }
}
