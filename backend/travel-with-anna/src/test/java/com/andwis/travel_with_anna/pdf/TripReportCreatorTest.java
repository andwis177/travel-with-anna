package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.note.Note;
import com.itextpdf.layout.element.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TripReportCreatorTest {
    @Mock
    private PdfFontFactory pdfFontFactory;
    private TripReportCreator tripReportCreator;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(pdfFontFactory.reportTitleFont()).thenReturn(null);
        when(pdfFontFactory.reportRegularFont()).thenReturn(null);
        when(pdfFontFactory.reportBoldFont()).thenReturn(null);
        when(pdfFontFactory.reportItalicFont()).thenReturn(null);

        tripReportCreator = new TripReportCreator(pdfFontFactory);
    }

    @Test
    void getTitle_returnsFormattedParagraph() throws IOException {
        // Given
        String tripTitle = "Test Trip";

        // When
        Paragraph paragraph = tripReportCreator.getTitle(tripTitle);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertEquals("Test Trip", extractedText);
    }

    @Test
    void getDates_returnsFormattedParagraph() throws IOException {
        // Given
        String startDate = "2024-01-01";
        String endDate = "2024-01-10";

        // When
        Paragraph paragraph = tripReportCreator.getDates(startDate, endDate);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertEquals("(2024-01-01 : 2024-01-10)", extractedText);
    }

    @Test
    void getDay_returnsFormattedParagraph() throws IOException {
        // Given
        Day day = Day.builder().date(LocalDate.of(2024, 1, 1)).build();

        // When
        Paragraph paragraph = tripReportCreator.getDay(day);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertEquals("2024-01-01 (MONDAY)", extractedText);
    }

    @Test
    void getActivity_returnsFormattedParagraph() throws IOException {
        // Given
        Activity activity = Activity.builder()
                .beginTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .badge("Work")
                .type("Task")
                .status("Complete")
                .activityTitle("Team Meeting")
                .build();

        // When
        Paragraph paragraph = tripReportCreator.getActivity(activity);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertTrue(extractedText.contains("09:00 - 10:00 | WORK (TASK) COMPLETE"));
        assertTrue(extractedText.contains("Team Meeting"));
    }

    @Test
    void getActivityAddress_returnsFormattedTable() throws IOException {
        // Given
        Address address = Address.builder()
                .place("Building 1")
                .address("123 Street")
                .city("Cityville")
                .country("Countryland")
                .phone("1234567890")
                .email("email@test.com")
                .website("www.test.com")
                .build();

        Activity activity = Activity.builder()
                .beginTime(LocalTime.of(9, 0))
                .address(address)
                .build();

        // When
        Paragraph paragraph = tripReportCreator.getActivityAddress(activity);
        Table table = (Table) paragraph.getChildren().getFirst();
        assertNotNull(table);

        List<String> extractedTexts = extractTextsFromTable(table);

        // Then
        assertEquals(6, extractedTexts.size());
        assertTrue(extractedTexts.contains("Building 1"));
        assertTrue(extractedTexts.contains("123 Street"));
        assertTrue(extractedTexts.contains("CITYVILLE, COUNTRYLAND"));
        assertTrue(extractedTexts.contains("www.test.com"));
        assertTrue(extractedTexts.contains("email@test.com"));
        assertTrue(extractedTexts.contains("1234567890"));
    }

    @Test
    void getNote_returnsFormattedParagraph() throws IOException {
        // Given
        Note note = new Note();
        note.setNote("This is a test note.");

        // When
        Paragraph paragraph = tripReportCreator.getNote(note);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertEquals("This is a test note.", extractedText);
    }

    @Test
    void getExpanse_returnsFormattedParagraph() throws IOException {
        // Given
        Expanse expanse = Expanse.builder()
                .expanseName("Flight Ticket")
                .price(BigDecimal.valueOf(500))
                .paid(BigDecimal.valueOf(300))
                .currency("USD")
                .build();

        // When
        Paragraph paragraph = tripReportCreator.getExpanse(expanse);
        String extractedText = extractText(paragraph);

        // Then
        assertNotNull(paragraph);
        assertTrue(extractedText.contains("PRICE: 500 USD"));
        assertTrue(extractedText.contains("PAID: 300 USD"));
        assertTrue(extractedText.contains("(Flight Ticket)"));
    }

    @Test
    void getTime_returnsFormattedTime() {
        // Given
        Activity activity = Activity.builder()
                .beginTime(LocalTime.of(9, 0))
                .endTime(null)
                .build();

        // When
        String time = tripReportCreator.getTime(activity);

        // Then
        assertEquals("09:00", time);

        activity.setEndTime(LocalTime.of(10, 0));
        time = tripReportCreator.getTime(activity);

        assertEquals("09:00 - 10:00", time);
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
}
