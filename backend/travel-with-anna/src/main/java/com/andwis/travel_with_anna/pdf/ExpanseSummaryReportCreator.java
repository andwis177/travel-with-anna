package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.trip.expanse.ExpanseByCurrency;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.SolidBorder;
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
import static com.itextpdf.layout.borders.Border.NO_BORDER;

@Service
@RequiredArgsConstructor
public class ExpanseSummaryReportCreator {
    private final PdfFontFactory pdfFontFactory;

    public Paragraph summaryByCurrency(String text) throws IOException {
        return new Paragraph(text)
                .setFont(pdfFontFactory.reportTitleFont())
                .setFontSize(12)
                .setFontColor(BLACK)
                .setMultipliedLeading(2.0f)
                .setTextAlignment(TextAlignment.CENTER);
    }

    public Paragraph createHeaderParagraph(String text) throws IOException {
        return new Paragraph(text)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(7)
                .setFontColor(WHITE)
                .setMultipliedLeading(0.4f)
                .setPadding(1);
    }

    public Paragraph getSummaryHeader(String tripCurrency) throws IOException {

        Table table = new Table(new float[]{70, 90, 90, 90, 90, 90});
        table.setWidth(UnitValue.createPointValue(520));

        table
                .addCell(new Cell().add(createHeaderParagraph("CURRENCY"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("PRICE/PAID"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("PRICE"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph(tripCurrency))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("PAID"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph(tripCurrency))
                        .setBorder(NO_BORDER));

        return new Paragraph().add(table).setBackgroundColor(GRAY);
    }

    public Paragraph getSummary(@NotNull ExpanseByCurrency expanse, String tripCurrency) throws IOException {
        Text summaryCurrencyColumnText = new Text(expanse.getCurrency())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Text summaryCurrencyText = new Text(expanse.getCurrency())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(7);

        Text tripCurrencyText = new Text(tripCurrency)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(7);

        Text pricePaidText = new Text(expanse.getTotalDebt().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Text priceText = new Text(expanse.getTotalPrice().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Text priceInTripCurrencyText = new Text(expanse.getTotalPriceInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Text paidText = new Text(expanse.getTotalPaid().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Text paidInTripCurrencyText = new Text(expanse.getTotalPaidInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Paragraph currencyName = new Paragraph()
                .add(summaryCurrencyColumnText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Paragraph pricePaid = new Paragraph()
                .add(pricePaidText)
                .add(" ")
                .add(summaryCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Paragraph price = new Paragraph()
                .add(priceText)
                .add(" ")
                .add(summaryCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Paragraph priceInTripCurrency = new Paragraph()
                .add(priceInTripCurrencyText)
                .add(" ")
                .add(tripCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Paragraph paid = new Paragraph()
                .add(paidText)
                .add(" ")
                .add(summaryCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Paragraph paidInTripCurrency = new Paragraph()
                .add(paidInTripCurrencyText)
                .add(" ")
                .add(tripCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Table table = new Table(new float[]{70, 90, 90, 90, 90, 90});
        table.setWidth(UnitValue.createPointValue(520));

        table
                .addCell(new Cell().add(currencyName)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(5))
                .addCell(new Cell().add(pricePaid)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(price)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(priceInTripCurrency)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paid)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paidInTripCurrency)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setTextAlignment(TextAlignment.RIGHT));

        return new Paragraph().add(table);
    }
}
