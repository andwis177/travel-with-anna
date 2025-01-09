package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.trip.expanse.ExpanseInTripCurrency;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.Border;
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

    private static final String TITLE_BUDGET = "BUDGET: ";
    private static final String TITLE_PRICE = "PRICE: ";
    private static final String TITLE_PRICE_PAID = "PRICE/PAID: ";
    private static final String TITLE_PAID = "PAID: ";

    private static final int SMALL_FONT_SIZE = 5;
    private static final int MEDIUM_FONT_SIZE = 6;
    private static final int BIG_FONT_SIZE = 7;
    private static final int AMOUNT_FONT_SIZE = 8;
    private static final float BIG_MULTIPLIED_LEADING = 0.5f;
    private static final float MEDIUM_MULTIPLIED_LEADING = 0.9f;
    private static final int SMALL_PADDING = 5;
    private static final int HEADER_PADDING = 1;
    private static final int TABLE_WIDTH = 520;
    private static final float[] TABLE_COLUMN_WIDTHS = new float[]{40, 90, 70, 66, 66, 56, 66, 66};

    public Paragraph getBudget(@NotNull BigDecimal budget, @NotNull ExpanseInTripCurrency expanseInTripCurrency,
                               String currency) throws IOException {
        Table table = createFullWidthTable();
        table.addCell(createStyledCell(createBudgetParagraph(TITLE_BUDGET, budget.toString(), currency), TextAlignment.LEFT))
                .addCell(createStyledCell(createBudgetParagraphWithNote(TITLE_PRICE, expanseInTripCurrency.price().toString(),
                        budget.subtract(expanseInTripCurrency.price()).toString(), currency), TextAlignment.RIGHT))
                .addCell(createStyledCell(createBudgetParagraph(TITLE_PRICE_PAID,
                        expanseInTripCurrency.price().subtract(expanseInTripCurrency.paid()).toString(), currency), TextAlignment.LEFT))
                .addCell(createStyledCell(createBudgetParagraphWithNote(TITLE_PAID, expanseInTripCurrency.paid().toString(),
                        budget.subtract(expanseInTripCurrency.paid()).toString(), currency), TextAlignment.RIGHT));

        return new Paragraph().add(table);
    }

    private Paragraph createBudgetParagraph(String title, String amount, String currency) throws IOException {
        return new Paragraph()
                .add(createStyledText(title, pdfFontFactory.reportRegularFont(), MEDIUM_FONT_SIZE))
                .add(createStyledText(amount, pdfFontFactory.reportBoldFont(), AMOUNT_FONT_SIZE))
                .add(" ")
                .add(createStyledText(currency, pdfFontFactory.reportRegularFont(), SMALL_FONT_SIZE))
                .setFontColor(ColorConstants.BLACK)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);
    }

    private Paragraph createBudgetParagraphWithNote(String title, String amount, String calculatedAmount,
                                                    String currency) throws IOException {
        return new Paragraph()
                .add(createStyledText(title, pdfFontFactory.reportRegularFont(), MEDIUM_FONT_SIZE))
                .add(createStyledText(amount, pdfFontFactory.reportBoldFont(), AMOUNT_FONT_SIZE))
                .add(" ")
                .add(createStyledText(currency, pdfFontFactory.reportRegularFont(), SMALL_FONT_SIZE))
                .add(" ( ")
                .add(createStyledText(calculatedAmount, pdfFontFactory.reportRegularFont(), AMOUNT_FONT_SIZE))
                .add(" ")
                .add(createStyledText(currency, pdfFontFactory.reportRegularFont(), SMALL_FONT_SIZE))
                .add(" )")
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(BIG_FONT_SIZE)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);
    }

    public Paragraph createHeaderParagraph(String text) throws IOException {
        return new Paragraph(text)
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE)
                .setFontColor(ColorConstants.WHITE)
                .setMultipliedLeading(BIG_MULTIPLIED_LEADING)
                .setPadding(SMALL_PADDING);
    }

    private Table createFullWidthTable() {
        return new Table(new float[2]).setWidth(UnitValue.createPercentValue(100));
    }

    private Cell createStyledCell(Paragraph content, TextAlignment alignment) {
        return new Cell()
                .add(content)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(alignment);
    }

    private Text createStyledText(String content, com.itextpdf.kernel.font.PdfFont font, int fontSize) {
        return new Text(content)
                .setFont(font)
                .setFontSize(fontSize);
    }

    public Paragraph getExpanseHeader(String tripCurrency) throws IOException {
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

        Table table = new Table(TABLE_COLUMN_WIDTHS);
        table.setWidth(UnitValue.createPointValue(TABLE_WIDTH));

        table
                .addCell(new Cell().add(createHeaderParagraph("DATE"))
                        .setBorder(NO_BORDER))
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

        Text expanseDateText = new Text(expanse.getDate())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(SMALL_FONT_SIZE);

        Text expanseCategoryText = new Text(expanse.getExpanseCategory())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text expanseNameText = new Text(expanse.getExpanseName())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text tripCurrencyTextSmall = new Text(tripCurrency)
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(SMALL_FONT_SIZE);

        Text currencyText = new Text(expanse.getCurrency())
                .setFont(pdfFontFactory.reportRegularFont())
                .setFontSize(SMALL_FONT_SIZE);

        Text priceText = new Text(expanse.getPrice().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text paidText = new Text(expanse.getPaid().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text exchangeRateText = new Text(expanse.getExchangeRate().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text priceInTripCurrencyText = new Text(expanse.getPriceInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Text paidInTripCurrencyText = new Text(expanse.getPaidInTripCurrency().toString())
                .setFont(pdfFontFactory.reportBoldFont())
                .setFontSize(MEDIUM_FONT_SIZE);

        Paragraph date = new Paragraph()
                .add(expanseDateText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.1f);

        Paragraph category = new Paragraph()
                .add(expanseCategoryText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Paragraph name = new Paragraph()
                .add(expanseNameText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(MEDIUM_MULTIPLIED_LEADING);

        Paragraph price = new Paragraph()
                .add(priceText)
                .add(" ")
                .add(currencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.0f);

        Paragraph paid = new Paragraph()
                .add(paidText)
                .add(" ")
                .add(currencyText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.0f);

        Paragraph exchangeRate = new Paragraph()
                .add(exchangeRateText)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.0f);

        Paragraph priceInTripCurrency = new Paragraph()
                .add(priceInTripCurrencyText)
                .add(" ")
                .add(tripCurrencyTextSmall)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.0f);

        Paragraph paidInTripCurrency = new Paragraph()
                .add(paidInTripCurrencyText)
                .add(" ")
                .add(tripCurrencyTextSmall)
                .setFontColor(BLACK)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(0.0f);

        Table table = new Table(TABLE_COLUMN_WIDTHS);
        table.setWidth(UnitValue.createPointValue(TABLE_WIDTH));

        table
                .addCell(new Cell().add(date)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setPadding(SMALL_PADDING))
                .addCell(new Cell().add(category)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.TOP)
                        .setWidth(88)
                        .setPadding(SMALL_PADDING))
                .addCell(new Cell().add(name)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setWidth(68)
                        .setPadding(SMALL_PADDING))
                .addCell(new Cell().add(price)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(64)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paid)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(64)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(exchangeRate)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(54)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(priceInTripCurrency)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(64)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .addCell(new Cell().add(paidInTripCurrency)
                        .setBorderRight(NO_BORDER)
                        .setBorderLeft(NO_BORDER)
                        .setBorderTop(NO_BORDER)
                        .setPadding(SMALL_PADDING)
                        .setWidth(64)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.RIGHT));
        return new Paragraph().add(table);
    }
}
