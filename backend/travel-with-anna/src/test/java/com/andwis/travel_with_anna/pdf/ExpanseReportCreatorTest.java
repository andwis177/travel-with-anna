package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.trip.expanse.ExpanseInTripCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
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

class ExpanseReportCreatorTest {
    @Mock
    private PdfFontFactory pdfFontFactory;

    private ExpanseReportCreator expanseReportCreator;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(pdfFontFactory.reportRegularFont()).thenReturn(null);
        when(pdfFontFactory.reportBoldFont()).thenReturn(null);
        expanseReportCreator = new ExpanseReportCreator(pdfFontFactory);
    }

    @Test
    void testGetBudget_returnsFormattedParagraphWithTable() throws IOException {
        // Given
        BigDecimal budget = new BigDecimal("1000.00");
        ExpanseInTripCurrency expanseInTripCurrency = new ExpanseInTripCurrency(
                new BigDecimal("800.00"),
                new BigDecimal("600.00")
        );
        String currency = "USD";

        // When
        Paragraph budgetParagraph = expanseReportCreator.getBudget(budget, expanseInTripCurrency, currency);
        assertNotNull(budgetParagraph, "Budget paragraph should not be null");

        Table table = (Table) budgetParagraph.getChildren().getFirst();
        assertNotNull(table, "Table within budget paragraph should not be null");

        List<String> extractedTexts = extractTextsFromTable(table);

        // Then
        assertNotNull(budgetParagraph);
        assertNotNull(table);
        assertTrue(extractedTexts.contains("BUDGET: 1000.00 USD"));
        assertTrue(extractedTexts.contains("PRICE: 800.00 USD ( 200.00 USD )"));
        assertTrue(extractedTexts.contains("PRICE/PAID: 200.00 USD"));
        assertTrue(extractedTexts.contains("PAID: 600.00 USD ( 400.00 USD )"));
    }

    @Test
    void testCreateHeaderParagraph_returnsFormattedParagraph() throws IOException {
        // Given
        String text = "HEADER TEXT";

        // When
        Paragraph headerParagraph = expanseReportCreator.createHeaderParagraph(text);
        String extractedText = extractText(headerParagraph);

        // Then
        assertNotNull(headerParagraph);
        assertEquals("HEADER TEXT", extractedText);
    }

    @Test
    void testGetExpanseHeader_returnsFormattedParagraphWithTable() throws IOException {
        // Given
        String tripCurrency = "EUR";

        // When
        Paragraph expanseHeader = expanseReportCreator.getExpanseHeader(tripCurrency);
        Table table = (Table) expanseHeader.getChildren().getFirst();
        List<String> extractedTexts = extractTextsFromTable(table);

        // Then
        assertNotNull(expanseHeader);
        assertNotNull(table);
        assertTrue(extractedTexts.contains("EXPANSE"));
        assertTrue(extractedTexts.contains("PRICE"));
        assertTrue(extractedTexts.contains("PAID"));
        assertTrue(extractedTexts.contains("EXCHANGE"));
        assertTrue(extractedTexts.contains("PRICE\n(EUR)"));
        assertTrue(extractedTexts.contains("PAID\n(EUR)"));
    }

    @Test
    void testGetExpanse_returnsFormattedParagraphWithTable() throws IOException {
        // Given
        ExpanseResponse expanse = ExpanseResponse.builder()
                .expanseId(1L)
                .expanseName("Hotel")
                .currency("USD")
                .expanseCategory("Stay")
                .date("2022-01-01")
                .price(new BigDecimal("200.00"))
                .paid(new BigDecimal("150.00"))
                .exchangeRate(new BigDecimal("1.1"))
                .priceInTripCurrency(new BigDecimal("220.00"))
                .paidInTripCurrency(new BigDecimal("165.00"))
                .build();
        String tripCurrency = "EUR";

        // When
        Paragraph expanseParagraph = expanseReportCreator.getExpanse(expanse, tripCurrency);
        Table table = (Table) expanseParagraph.getChildren().getFirst();
        List<String> extractedTexts = extractTextsFromTable(table);
        System.out.println(extractedTexts);

        // Then
        assertNotNull(expanseParagraph);
        assertNotNull(table);
        assertTrue(extractedTexts.contains("Hotel"));
        assertTrue(extractedTexts.contains("Stay"));
        assertTrue(extractedTexts.contains("2022-01-01"));
        assertTrue(extractedTexts.contains("200.00 USD"));
        assertTrue(extractedTexts.contains("150.00 USD"));
        assertTrue(extractedTexts.contains("1.1"));
        assertTrue(extractedTexts.contains("220.00 EUR"));
        assertTrue(extractedTexts.contains("165.00 EUR"));
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