package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.itextpdf.layout.element.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ExpanseSummaryReportCreatorTest {
    @Mock
    private PdfFontFactory pdfFontFactory;

    private ExpanseSummaryReportCreator expanseSummaryReportCreator;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(pdfFontFactory.reportTitleFont()).thenReturn(null);
        when(pdfFontFactory.reportRegularFont()).thenReturn(null);
        when(pdfFontFactory.reportBoldFont()).thenReturn(null);
        when(pdfFontFactory.reportItalicFont()).thenReturn(null);

        expanseSummaryReportCreator = new ExpanseSummaryReportCreator(pdfFontFactory);
    }

    @Test
    void summaryByCurrency_returnsFormattedParagraph() throws IOException {
        // Given
        String text = "Summary by Currency";

        // When
        Paragraph paragraph = expanseSummaryReportCreator.summaryByCurrency(text);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertEquals("Summary by Currency", extractedText);
    }

    @Test
    void createHeaderParagraph_returnsFormattedParagraph() throws IOException {
        // Given
        String text = "HEADER";

        // When
        Paragraph paragraph = expanseSummaryReportCreator.createHeaderParagraph(text);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertEquals("HEADER", extractedText);
    }

    @Test
    void getSummaryHeader_returnsFormattedParagraphWithTable() throws IOException {
        // Given
        String tripCurrency = "USD";

        // When
        Paragraph paragraph = expanseSummaryReportCreator.getSummaryHeader(tripCurrency);
        Table table = (Table) paragraph.getChildren().getFirst();
        List<String> extractedTexts = extractTextsFromTable(table);

        // Then
        assertNotNull(paragraph);
        assertNotNull(table);
        assertTrue(extractedTexts.contains("CURRENCY"));
        assertTrue(extractedTexts.contains("PRICE/PAID"));
        assertTrue(extractedTexts.contains("PRICE"));
        assertTrue(extractedTexts.contains("PAID"));
        assertTrue(extractedTexts.contains(tripCurrency));
    }

    @Test
    void getSummary_returnsFormattedParagraphWithTable() throws IOException {
        // Given
        ExpanseByCurrency expanse = ExpanseByCurrency.builder()
                .currency("EUR")
                .totalDebt(BigDecimal.valueOf(100))
                .totalPrice(BigDecimal.valueOf(200))
                .totalPriceInTripCurrency(BigDecimal.valueOf(210))
                .totalPaid(BigDecimal.valueOf(150))
                .totalPaidInTripCurrency(BigDecimal.valueOf(160))
                .build();
        String tripCurrency = "USD";

        // When
        Paragraph paragraph = expanseSummaryReportCreator.getSummary(expanse, tripCurrency);
        Table table = (Table) paragraph.getChildren().getFirst();
        List<String> extractedTexts = extractTextsFromTable(table);

        // Then
        assertNotNull(paragraph);
        assertNotNull(table);
        assertTrue(extractedTexts.contains("EUR"));
        assertTrue(extractedTexts.contains("100 EUR"));
        assertTrue(extractedTexts.contains("200 EUR"));
        assertTrue(extractedTexts.contains("210 USD"));
        assertTrue(extractedTexts.contains("150 EUR"));
        assertTrue(extractedTexts.contains("160 USD"));
    }

    private @NotNull String extractText(@NotNull Paragraph paragraph) {
        StringBuilder sb = new StringBuilder();
        for (Object element : paragraph.getChildren()) {
            if (element instanceof Text) {
                sb.append(((Text) element).getText());
            }
        }
        return sb.toString();
    }

    private @NotNull List<String> extractTextsFromTable(@NotNull Table table) {
        List<String> texts = new java.util.ArrayList<>();
        for (int rowIndex = 0; rowIndex < table.getNumberOfRows(); rowIndex++) {
            for (int colIndex = 0; colIndex < table.getNumberOfColumns(); colIndex++) {
                Cell cell = table.getCell(rowIndex, colIndex);
                if (cell != null) {
                    for (IElement element : cell.getChildren()) {
                        if (element instanceof Paragraph) {
                            texts.add(extractText((Paragraph) element));
                        }
                    }
                }
            }
        }
        return texts;
    }
}