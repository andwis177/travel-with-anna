package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.trip.expanse.ExpanseInTripCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
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
import java.math.BigDecimal;

import static com.itextpdf.kernel.colors.ColorConstants.*;
import static com.itextpdf.layout.borders.Border.NO_BORDER;

@Service
@RequiredArgsConstructor
public class ExpanseReportCreator {
    private final PdfFontFactory pdfFontFactory;

    public Paragraph getBudget(
            @NotNull BigDecimal budget,
            @NotNull ExpanseInTripCurrency expanseInTripCurrency,
            String currency)
            throws IOException {

        Table table = new Table(new float[]{1, 1});
        table.setWidth(UnitValue.createPercentValue(100));

        table
                .addCell(new Cell().add(createBudgetParagraph(
                                "BUDGET: ",
                                budget.toString(),
                                currency))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT))
                .addCell(new Cell().add(createBudgetParagraphWithNote(
                                "PRICE: ",
                                expanseInTripCurrency.price().toString(),
                                budget.subtract(expanseInTripCurrency.price()).toString(),
                                currency))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(createBudgetParagraph(
                                "PRICE/PAID: ",
                                expanseInTripCurrency.price().subtract(expanseInTripCurrency.paid()).toString(),
                                currency))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT))
                .addCell(new Cell().add(createBudgetParagraphWithNote(
                                "PAID: ",
                                expanseInTripCurrency.paid().toString(),
                                budget.subtract(expanseInTripCurrency.paid()).toString(),
                                currency))
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.RIGHT));

        return new Paragraph().add(table);
    }

    private Paragraph createBudgetParagraph(String title, String budget, String currency) throws IOException {
        Text titleText = new Text(title)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text budgetText = new Text(budget)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Text currencyText = new Text(currency)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(7);

        return new Paragraph()
                .add(titleText)
                .add(budgetText)
                .add(" ")
                .add(currencyText)
                .setFontColor(BLACK)
                .setMultipliedLeading(1.0f);
    }

    private Paragraph createBudgetParagraphWithNote(String title, String budget, String calculatedAmount, String currency) throws IOException {
        Text titleText = new Text(title)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text budgetText = new Text(budget)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(10);

        Text calculatedAmountText = new Text(calculatedAmount)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(9);

        Text currencyText = new Text(currency)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(7);

        return new Paragraph()
                .add(titleText)
                .add(budgetText)
                .add(" ")
                .add(currencyText)
                .add(" (")
                .add(calculatedAmountText)
                .add(" ")
                .add(currencyText)
                .add(")")
                .setFontColor(BLACK)
                .setMultipliedLeading(1.0f);
    }

    public Paragraph createHeaderParagraph(String text) throws IOException {
        return new Paragraph(text)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(7)
                .setFontColor(WHITE)
                .setMultipliedLeading(1.8f)
                .setPadding(1);
    }

    public Paragraph getExpanseHeader(String tripCurrency) throws IOException {
        Text priceText = new Text("PRICE")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(7);

        Text paidText = new Text("PAID")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(7);

        Text currency = new Text(tripCurrency)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(6)
                ;

        Paragraph priceHeader = new Paragraph()
                .add(priceText)
                .add("\n(")
                .add(currency)
                .add(")")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(6)
                .setFontColor(WHITE)
                .setMultipliedLeading(1.0f)
                .setPadding(1);

        Paragraph paidHeader = new Paragraph()
                .add(paidText)
                .add("\n(")
                .add(currency)
                .add(")")
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(6)
                .setFontColor(WHITE)
                .setMultipliedLeading(1.0f)
                .setPadding(1);

        Table table = new Table(new float[]{100, 70, 70, 70, 70, 70, 70});
        table.setWidth(UnitValue.createPointValue(520));

        table
                .addCell(new Cell().add(createHeaderParagraph("EXPANSE"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("DESCRIPTION"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("PRICE"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("PAID"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(createHeaderParagraph("EXCHANGE"))
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(priceHeader)
                        .setBorder(NO_BORDER))
                .addCell(new Cell().add(paidHeader)
                        .setBorder(NO_BORDER));

        return new Paragraph().add(table).setBackgroundColor(GRAY);
    }

    public Paragraph getExpanse(@NotNull ExpanseResponse expanse, String tripCurrency) throws IOException {
        Text expanseCategoryText = new Text(expanse.expanseCategory())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text expanseNameText = new Text(expanse.expanseName())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text tripCurrencyText = new Text(tripCurrency)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(8);

        Text currencyText = new Text(expanse.currency())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(6);

        Text priceText = new Text(expanse.price().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(8);

        Text paidText = new Text(expanse.paid().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(8);

        Text exchangeRateText = new Text(expanse.exchangeRate().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(8);

        Text priceInTripCurrencyText = new Text(expanse.priceInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(8);

        Text paidInTripCurrencyText = new Text(expanse.paidInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(8);

        Paragraph category = new Paragraph()
                .add(expanseCategoryText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(1.0f);

        Paragraph name = new Paragraph()
                .add(expanseNameText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(1.0f);

        Paragraph price = new Paragraph()
                .add(priceText)
                .add(" ")
                .add(currencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Paragraph paid = new Paragraph()
                .add(paidText)
                .add(" ")
                .add(currencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Paragraph exchangeRate = new Paragraph()
                .add(exchangeRateText)
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

        Paragraph paidInTripCurrency = new Paragraph()
                .add(paidInTripCurrencyText)
                .add(" ")
                .add(tripCurrencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.4f);

        Table table = new Table(new float[]{100, 70, 70, 70, 70, 70, 70});
        table.setWidth(UnitValue.createPointValue(520));

        table
                .addCell(new Cell().add(category)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                        .setWidth(95)
                        .setPadding(2))
                .addCell(new Cell().add(name)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setWidth(65)
                        .setPadding(2))
                .addCell(new Cell().add(price)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paid)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(exchangeRate)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(priceInTripCurrency)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paidInTripCurrency)
                        .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.1f))
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(5)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT));

        return new Paragraph().add(table);
    }
}
