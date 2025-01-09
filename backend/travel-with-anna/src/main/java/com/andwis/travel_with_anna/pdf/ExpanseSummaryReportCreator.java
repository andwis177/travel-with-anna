package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.itextpdf.kernel.colors.ColorConstants.*;
import static com.itextpdf.layout.borders.Border.NO_BORDER;

@Service
@RequiredArgsConstructor
public class ExpanseSummaryReportCreator {

    private static final int SMALL_FONT_SIZE = 5;
    private static final int MEDIUM_FONT_SIZE = 6;
    private static final int BIG_FONT_SIZE = 7;
    private static final int HEADER_FONT_SIZE = 9;
    private static final float MEDIUM_MULTIPLIED_LEADING = 1.0f;
    private static final float BIG_MULTIPLIED_LEADING = 1.0f;
    private static final int SMALL_PADDING = 5;
    private static final int HEADER_PADDING = 1;
    private static final int TABLE_WIDTH = 520;
    private static final float[] TABLE_COLUMN_WIDTHS = new float[]{60, 92, 92, 92, 92, 92};

    private final PdfFontFactory pdfFontFactory;

    public Paragraph summaryByCurrency(String text) throws IOException {
        return new Paragraph(text)
                .setFont(pdfFontFactory.reportTitleFont())
                .setFontSize(HEADER_FONT_SIZE)
                .setFontColor(BLACK)
                .setMultipliedLeading(BIG_MULTIPLIED_LEADING)
                .setTextAlignment(TextAlignment.CENTER);
    }

    public Paragraph createHeaderParagraph(String text) throws IOException {
        return new Paragraph(text)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE)
                .setFontColor(WHITE)
                .setMultipliedLeading(BIG_MULTIPLIED_LEADING)
                .setPadding(SMALL_PADDING);
    }

    public Paragraph getSummaryHeader(String tripCurrency) throws IOException {
        Text priceText = new Text("PRICE")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text paidText = new Text("PAID")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text currency = new Text(tripCurrency)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(SMALL_FONT_SIZE);

        Paragraph priceHeader = new Paragraph()
                .add(priceText)
                .add("\n(")
                .add(currency)
                .add(")")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(SMALL_FONT_SIZE)
                .setFontColor(WHITE)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING)
                .setPadding(HEADER_PADDING);

        Paragraph paidHeader = new Paragraph()
                .add(paidText)
                .add("\n(")
                .add(currency)
                .add(")")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(SMALL_FONT_SIZE)
                .setFontColor(WHITE)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING)
                .setPadding(HEADER_PADDING);

        Paragraph price_paid = new Paragraph()
                .add(priceText)
                .add("\n")
                .add(paidText)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(SMALL_FONT_SIZE)
                .setFontColor(WHITE)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING)
                .setPadding(HEADER_PADDING);

        Table table = new Table(TABLE_COLUMN_WIDTHS);
        table.setWidth(UnitValue.createPointValue(TABLE_WIDTH));

        table
                .addCell(new Cell().add(createHeaderParagraph("CURRENCY"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(price_paid)
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("PRICE"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("PAID"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(priceHeader)
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(paidHeader)
                        .setBorder(NO_BORDER));

        return new Paragraph().add(table).setBackgroundColor(GRAY);
    }

    public Paragraph getSummary(@NotNull ExpanseByCurrency expanse, String tripCurrency) throws IOException {
        Text summaryCurrencyColumnText = new Text(expanse.getCurrencyCode())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(BIG_FONT_SIZE);

        Text summaryCurrencyText = new Text(expanse.getCurrencyCode())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(SMALL_FONT_SIZE);

        Text tripCurrencyText = new Text(tripCurrency)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(SMALL_FONT_SIZE);

        Text pricePaidText = new Text(expanse.getTotalDebt().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text priceText = new Text(expanse.getTotalPrice().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text priceInTripCurrencyText = new Text(expanse.getTotalPriceInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text paidText = new Text(expanse.getTotalPaid().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text paidInTripCurrencyText = new Text(expanse.getTotalPaidInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Paragraph currencyName = new Paragraph()
                .add(summaryCurrencyColumnText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Paragraph pricePaid = new Paragraph()
                .add(pricePaidText)
                .add(" ")
                .add(summaryCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Paragraph price = new Paragraph()
                .add(priceText)
                .add(" ")
                .add(summaryCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Paragraph priceInTripCurrency = new Paragraph()
                .add(priceInTripCurrencyText)
                .add(" ")
                .add(tripCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Paragraph paid = new Paragraph()
                .add(paidText)
                .add(" ")
                .add(summaryCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Paragraph paidInTripCurrency = new Paragraph()
                .add(paidInTripCurrencyText)
                .add(" ")
                .add(tripCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Table table = new Table(TABLE_COLUMN_WIDTHS);
        table.setWidth(UnitValue.createPointValue(TABLE_WIDTH));

        table
                .addCell(new Cell().add(currencyName)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setPadding(SMALL_PADDING))
                .addCell(new Cell().add(pricePaid)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(58)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(price)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(90)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paid)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(90)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(priceInTripCurrency)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(90)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paidInTripCurrency)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(90)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                        .setTextAlignment(TextAlignment.RIGHT));

        return new Paragraph().add(table);
    }
}
