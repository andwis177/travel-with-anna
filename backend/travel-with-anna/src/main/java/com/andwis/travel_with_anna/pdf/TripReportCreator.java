package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.note.Note;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.itextpdf.kernel.colors.ColorConstants.*;

@Service
@RequiredArgsConstructor
public class TripReportCreator {

    private final PdfFontFactory pdfFontFactory;
    private final AddressParagraphCreator addressParagraphCreator;

    private static final int FONT_SIZE_2 = 6;
    private static final int FONT_SIZE_3 = 7;
    private static final int FONT_SIZE_4 = 8;
    private static final int FONT_SIZE_5 = 9;
    private static final int FONT_SIZE_6 = 16;
    private static final float SMALL_TEXT_LINE = 0.7f;

    public Paragraph getSeparatorLine() throws IOException {
        return new Paragraph()
                .add("-")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(2)
                .setFontColor(GRAY)
                .setBackgroundColor(GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(0.1f);
    }

    public Paragraph getTitle(String tripTitle) throws IOException {
        return new Paragraph()
                .add(tripTitle)
                .setFont(pdfFontFactory.reportTitleFont())
                .setFontSize(FONT_SIZE_6)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(SMALL_TEXT_LINE);
    }

    public Paragraph getDates(String startDate, String endDate) throws IOException {
        Text startDateText = new Text(startDate)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_3);

        Text endDateText = new Text(endDate)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_3);

        return new Paragraph()
                .add("(")
                .add(startDateText)
                .add(" : ")
                .add(endDateText)
                .add(")")
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(FONT_SIZE_3)
                .setMultipliedLeading(SMALL_TEXT_LINE)
                .setMarginBottom(5);
    }

    public Paragraph getDay(@NotNull Day day) throws IOException {
        Text dateText = new Text(day.getDate().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(FONT_SIZE_5)
                .setFontColor(WHITE);

        Text dayOfWeekText = new Text(day.getDate().getDayOfWeek().toString())
                .setFont(pdfFontFactory.reportRegularFont());

        return new Paragraph()
                .add(dateText)
                .add(" (")
                .add(dayOfWeekText)
                .add(")")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_4)
                .setFontColor(WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(GRAY);
    }

    public Paragraph getDayNote(@NotNull Note note) throws IOException {
        return new Paragraph()
                .add(note.getContent())
                .setFont(pdfFontFactory.reportItalicFont())
                .setFontSize(FONT_SIZE_3)
                .setFontColor(BLACK)
                .setMultipliedLeading(SMALL_TEXT_LINE)
                .setTextAlignment(TextAlignment.CENTER);
    }

    public Paragraph getActivityAddress(@NotNull Activity activity) throws IOException {

        Table table = new Table(new float[]{50, 50}).setWidth(UnitValue.createPercentValue(100));
        table.addCell(createCell(addressParagraphCreator.createReportAddress(activity), TextAlignment.LEFT))
                .addCell(createCell(addressParagraphCreator.creatReportContactInf(activity), TextAlignment.CENTER));

        return new Paragraph().add(table);
    }

    public Paragraph getNote(@NotNull Note note) throws IOException {
        return new Paragraph()
                .add(note.getContent())
                .setFont(pdfFontFactory.reportItalicFont())
                .setFontSize(FONT_SIZE_3)
                .setFontColor(BLACK)
                .setMultipliedLeading(SMALL_TEXT_LINE)
                .setMarginBottom(5)
                .setTextAlignment(TextAlignment.LEFT);
    }

    public Paragraph getExpanse(@NotNull Expanse expanse) throws IOException {

        Text expanseNameText = new Text(" ")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_3);

        if (expanse.getExpanseName() != null && !expanse.getExpanseName().isEmpty() && !expanse.getExpanseName().isBlank()) {
            expanseNameText = new Text(" (" + expanse.getExpanseName() + ")");
        }

        Text currencyText = new Text(expanse.getCurrency())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_3);

        Text priceValueText = new Text(expanse.getPrice().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(FONT_SIZE_4);

        Text priceText = new Text("  PRICE: ")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_2);

        Text paidValueText = new Text(expanse.getPaid().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(FONT_SIZE_4);

        Text paidText = new Text(" | PAID: ")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_2);

        return new Paragraph()
                .add(priceText)
                .add(priceValueText)
                .add(" ")
                .add(currencyText)
                .add(paidText)
                .add(paidValueText)
                .add(" ")
                .add(currencyText)
                .add(expanseNameText)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(FONT_SIZE_4)
                .setFontColor(BLACK)
                .setMultipliedLeading(SMALL_TEXT_LINE)
                .setTextAlignment(TextAlignment.CENTER);
    }

    public String getTime(@NotNull Activity activity) {
        if (activity.getFormattedEndTime().isEmpty()) {
            return activity.getFormattedBeginTime();
        } else {
            return activity.getFormattedBeginTime() + " - " + activity.getFormattedEndTime();
        }
    }

    private Cell createCell(Paragraph paragraph, TextAlignment alignment) {
        return new Cell().add(paragraph).setBorder(Border.NO_BORDER).setTextAlignment(alignment);
    }
}
