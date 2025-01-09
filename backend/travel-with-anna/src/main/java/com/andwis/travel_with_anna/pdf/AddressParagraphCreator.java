package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.itextpdf.kernel.font.PdfFont;
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

import static com.itextpdf.kernel.colors.ColorConstants.BLACK;

@Service
@RequiredArgsConstructor
public class AddressParagraphCreator {

    private final PdfFontFactory pdfFontFactory;

    private static final int FONT_SIZE_2 = 6;
    private static final int FONT_SIZE_3 = 7;
    private static final float TEXT_LEADING = 0.9f;
    private static final int CELL_WIDTH = 200;

    private Paragraph createParagraphWithText(Text text, TextAlignment alignment) {
        return new Paragraph().add(text)
                .setFontColor(BLACK)
                .setMultipliedLeading(TEXT_LEADING)
                .setTextAlignment(alignment);
    }

    private Text createText(String content, PdfFont font){
        return new Text(content).setFont(font).setFontSize((float) FONT_SIZE_3);
    }

    private Text createSmallText(String content, PdfFont font){
        return new Text(content).setFont(font).setFontSize((float) FONT_SIZE_2);
    }

    private Paragraph createParagraphForCityAndCountry(Text city, Text country) {
        return new Paragraph()
                .add(city)
                .add(" ")
                .add(country)
                .setFontColor(BLACK)
                .setMultipliedLeading(TEXT_LEADING);
    }

    private Cell createCell(Paragraph paragraph, TextAlignment alignment) {
        return new Cell().add(paragraph).setBorder(Border.NO_BORDER).setTextAlignment(alignment);
    }

    public Paragraph createReportAddress(@NotNull Activity activity) throws IOException {
        Address address = activity.getAddress();

        Paragraph nameParagraph = createParagraphWithText(
                createText(address.getPlace(), pdfFontFactory.reportBoldFont()), TextAlignment.LEFT)
                .setMultipliedLeading(TEXT_LEADING);

        Paragraph addressParagraph = createParagraphWithText(
                createText(address.getAddress(), pdfFontFactory.reportRegularFont()), TextAlignment.LEFT)
                .setMultipliedLeading(TEXT_LEADING);

        Paragraph cityCountryParagraph =
                createParagraphForCityAndCountry(createText(address.getCity().toUpperCase(),
                        pdfFontFactory.reportBoldFont()), createSmallText(address.getCountry().toUpperCase(),
                        pdfFontFactory.reportItalicFont()))
                        .setMultipliedLeading(TEXT_LEADING);

        Table table = new Table(new float[]{1}).setWidth(UnitValue.createPercentValue(100));
        table.addCell(createCell(nameParagraph, TextAlignment.LEFT)
                )
                .addCell(createCell(addressParagraph, TextAlignment.LEFT))
                .addCell(createCell(cityCountryParagraph, TextAlignment.LEFT));

        return new Paragraph().add(table);
    }

    public Paragraph creatReportContactInf(@NotNull Activity activity) throws IOException {
        Address address = activity.getAddress();

        Paragraph websiteParagraph = createParagraphWithText(
                createText(address.getWebsite(), pdfFontFactory.reportRegularFont()), TextAlignment.CENTER)
                .setMultipliedLeading(TEXT_LEADING)
                .setWidth(CELL_WIDTH);

        Paragraph phoneParagraph = createParagraphWithText(
                createText(address.getPhoneNumber(), pdfFontFactory.reportRegularFont()), TextAlignment.CENTER)
                .setMultipliedLeading(TEXT_LEADING)
                .setWidth(CELL_WIDTH);

        Paragraph emailParagraph = createParagraphWithText(
                createText(address.getEmail(), pdfFontFactory.reportRegularFont()), TextAlignment.CENTER)
                .setMultipliedLeading(TEXT_LEADING)
                .setWidth(CELL_WIDTH);

        Table table = new Table(new float[]{1}).setWidth(UnitValue.createPercentValue(50));
        table.addCell(createCell(websiteParagraph, TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setMaxWidth(CELL_WIDTH))
                .addCell(createCell(emailParagraph, TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setMaxWidth(CELL_WIDTH))
                .addCell(createCell(phoneParagraph, TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setMaxWidth(CELL_WIDTH));

        return new Paragraph().add(table);
    }
}
