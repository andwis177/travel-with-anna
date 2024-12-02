package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.handler.exception.PdfReportCreationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/pdf/reports")
@RequiredArgsConstructor
@Tag(name = "Pdf Report")
public class PdfReportController {
    private final PdfReportService pdfReportService;

    @GetMapping(value = "/trip/{tripId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateTripPdfReport(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails connectedUser) throws PdfReportCreationException {
        ByteArrayOutputStream pdfReport = pdfReportService.createTripPdfReport(tripId, connectedUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport.toByteArray());
    }

    @GetMapping(value = "/expanse/{tripId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateExpansePdfReport(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails connectedUser) throws PdfReportCreationException {
        ByteArrayOutputStream pdfReport = pdfReportService.createExpansePdfReport(tripId, connectedUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfReport.toByteArray());
    }
}
