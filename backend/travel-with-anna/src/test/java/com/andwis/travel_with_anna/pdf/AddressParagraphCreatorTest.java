package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.itextpdf.layout.element.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AddressParagraphCreatorTest {
    @Mock
    private PdfFontFactory pdfFontFactory;

    private AddressParagraphCreator addressParagraphCreator;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(pdfFontFactory.reportRegularFont()).thenReturn(null);
        when(pdfFontFactory.reportBoldFont()).thenReturn(null);
        when(pdfFontFactory.reportItalicFont()).thenReturn(null);

        addressParagraphCreator = new AddressParagraphCreator(pdfFontFactory);
    }

    @Test
    void createReportAddress_returnsFormattedTable() throws IOException {
        // Given
        Address address = Address.builder()
                .place("Building 1")
                .address("123 Street")
                .city("Cityville")
                .country("Countryland")
                .build();

        Activity activity = Activity.builder()
                .address(address)
                .build();

        // When
        Paragraph paragraph = addressParagraphCreator.createReportAddress(activity);
        Table table = (Table) paragraph.getChildren().getFirst();
        assertNotNull(table);

        List<String> extractedTexts = extractTextsFromTable(table);

        // Then
        assertEquals(3, extractedTexts.size());
        assertTrue(extractedTexts.contains("Building 1"));
        assertTrue(extractedTexts.contains("123 Street"));
        assertTrue(extractedTexts.contains("CITYVILLE COUNTRYLAND"));
    }

    @Test
    void creatReportContactInf_returnsFormattedTable() throws IOException {
        // Given
        Address address = Address.builder()
                .phoneNumber("1234567890")
                .email("email@test.com")
                .website("www.test.com")
                .build();

        Activity activity = Activity.builder()
                .address(address)
                .build();

        // When
        Paragraph paragraph = addressParagraphCreator.creatReportContactInf(activity);
        Table table = (Table) paragraph.getChildren().getFirst();
        assertNotNull(table);

        List<String> extractedTexts = extractTextsFromTable(table);

        // Then
        assertEquals(3, extractedTexts.size());
        assertTrue(extractedTexts.contains("1234567890"));
        assertTrue(extractedTexts.contains("email@test.com"));
        assertTrue(extractedTexts.contains("www.test.com"));
    }

    private @NotNull List<String> extractTextsFromTable(@NotNull Table table) {
        List<String> texts = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < table.getNumberOfRows(); rowIndex++) {
            for (int colIndex = 0; colIndex < table.getNumberOfColumns(); colIndex++) {
                Cell cell = table.getCell(rowIndex, colIndex);
                assertNotNull(cell, "Cell should not be null for row " + rowIndex + " and column " + colIndex);
                for (IElement element : cell.getChildren()) {
                    if (element instanceof Paragraph childParagraph) {
                        texts.add(extractText(childParagraph));
                    }
                }
            }
        }
        return texts;
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
}