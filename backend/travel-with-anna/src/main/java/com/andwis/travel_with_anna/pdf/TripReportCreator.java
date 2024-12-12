package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.address.Address;
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
import java.sql.Time;

import static com.itextpdf.kernel.colors.ColorConstants.*;

@Service
@RequiredArgsConstructor
public class TripReportCreator {
    private final PdfFontFactory pdfFontFactory;

    public Paragraph getSeparatorLine() throws IOException {
        return new Paragraph()
                .add("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(6)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(0.0f);
    }

    public Paragraph getTitle(String tripTitle) throws IOException {
        return new Paragraph()
                .add(tripTitle)
                .setFont(pdfFontFactory.reportTitleFont())
                .setFontSize(18)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMultipliedLeading(0.7f);
    }

    public Paragraph getDates(String startDate, String endDate) throws IOException {
        Text startDateText = new Text(startDate)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text endDateText = new Text(endDate)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        return new Paragraph()
                .add("(")
                .add(startDateText)
                .add(" : ")
                .add(endDateText)
                .add(")")
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setMultipliedLeading(0.7f)
                .setMarginBottom(5);
    }

    public Paragraph getDay(@NotNull Day day) throws IOException {
        Text dateText = new Text(day.getDate().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10)
                .setFontColor(WHITE);

        Text dayOfWeekText = new Text(day.getDate().getDayOfWeek().toString())
                .setFont(pdfFontFactory.reportRegularFont());

        return new Paragraph()
                .add(dateText)
                .add(" (")
                .add(dayOfWeekText)
                .add(")")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9)
                .setFontColor(WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(GRAY);
    }

    public Paragraph getDayNote(@NotNull Note note) throws IOException {
        return new Paragraph()
                .add(note.getNote())
                .setFont(pdfFontFactory.reportItalicFont())
                .setFontSize(8)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.CENTER);

    }
    public Paragraph getActivity(Activity activity) throws IOException {
        Text timeText = new Text(getTime(activity))
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(9);

        Text badgeText = new Text(activity.getBadge().toUpperCase())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(9);

        Text typeText = new Text(activity.getType().toUpperCase())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        Text statusText = new Text(" ")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        if (activity.getStatus() != null && !activity.getStatus().isEmpty() && !activity.getStatus().isBlank()) {
            statusText = new Text(activity.getStatus().toUpperCase())
                    .setFont(pdfFontFactory.reportRegularFont())
                    .setFontSize(8);
        }


        if (activity.getActivityTitle() != null &&
                !activity.getActivityTitle().isEmpty() &&
                !activity.getActivityTitle().isBlank()) {
            activity.setActivityTitle("   -   " + activity.getActivityTitle() );
        }

        Text activityText = new Text(activity.getActivityTitle())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        return new Paragraph()
                .add(timeText)
                .add(" | ")
                .add(badgeText)
                .add(" (")
                .add(typeText)
                .add(") ")
                .add(statusText)
                .add(activityText)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.LEFT);
    }

    public Paragraph getActivityAddress(@NotNull Activity activity) throws IOException {
        Text placeText = new Text(activity.getAddress().getPlace())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        Paragraph addressName = new Paragraph()
                .add(placeText)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.LEFT);

        Text addressText = new Text(activity.getAddress().getAddress())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        Paragraph address = new Paragraph()
                .add(addressText)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.LEFT);

        checkIfTextNullOrEmpty(activity.getAddress());

        Text cityText = new Text(activity.getAddress().getCity().toUpperCase())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(9);

        Text countryText = new Text(activity.getAddress().getCountry().toUpperCase())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        Paragraph cityCountry = new Paragraph()
                .add(cityText)
                .add(countryText)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.LEFT);

        Text websiteText = new Text(activity.getAddress().getWebsite())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        Paragraph websiteParagraph = new Paragraph()
                .add(websiteText)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.CENTER);

        Text phoneText = new Text(activity.getAddress().getPhone())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Paragraph phoneParagraph = new Paragraph()
                .add(phoneText)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.CENTER);

        Text emailText = new Text(activity.getAddress().getEmail())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        Paragraph emailParagraph = new Paragraph()
                .add(emailText)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setTextAlignment(TextAlignment.CENTER);

        Table table = new Table(new float[]{1, 1});
        table.setWidth(UnitValue.createPercentValue(100));

        table
                .addCell(new Cell().add(addressName)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT))
                .addCell(new Cell().add(websiteParagraph)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.CENTER))
                .addCell(new Cell().add(address)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT))
                .addCell(new Cell().add(emailParagraph)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.CENTER))
                .addCell(new Cell().add(cityCountry)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT))
                .addCell(new Cell().add(phoneParagraph)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.CENTER));

        return new Paragraph().add(table);
    }

    public Paragraph getNote(@NotNull Note note) throws IOException {
        return new Paragraph()
                .add(note.getNote())
                .setFont(pdfFontFactory.reportItalicFont())
                .setFontSize(8)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.7f)
                .setMarginBottom(5)
                .setTextAlignment(TextAlignment.LEFT);
    }

    public Paragraph getExpanse(@NotNull Expanse expanse) throws IOException {

        if (expanse.getExpanseName() != null && !expanse.getExpanseName().isEmpty() && !expanse.getExpanseName().isBlank()) {
            expanse.setExpanseName("  (" + expanse.getExpanseName() + ")");
        }

        Text expanseNameText = new Text(expanse.getExpanseName())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text currencyText = new Text(expanse.getCurrency())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text priceValueText = new Text(expanse.getPrice().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(9);

        Text priceText = new Text("  PRICE: ")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(7);

        Text paidValueText = new Text(expanse.getPaid().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(9);

        Text paidText = new Text(" | PAID: ")
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(7);

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
                .setFontSize(9)
                .setFontColor(BLACK)
                .setMultipliedLeading(0.6f)
                .setTextAlignment(TextAlignment.CENTER);
    }

    public String getTime(@NotNull Activity activity) {
        if (activity.getEndTime().isEmpty()) {
            return activity.getBeginTime();
        } else {
            return activity.getBeginTime() + " - " + activity.getEndTime();
        }
    }

    private void checkIfTextNullOrEmpty(@NotNull Address address){
        String city = address.getCity();
        if (city == null || city.isBlank()) {
            address.setCity(" ");
        } else {
            address.setCity(city.toUpperCase() + ", ");
        }
        String country = address.getCountry();
        if (country == null || country.isBlank()) {
            address.setCountry(" ");
        }
    }
}
