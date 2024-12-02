package com.andwis.travel_with_anna.pdf;

import com.itextpdf.kernel.font.PdfFont;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfFontFactory {

    public PdfFont reportTitleFont() throws IOException {
        return com.itextpdf.kernel.font.PdfFontFactory.createFont("Courier");
    }

    public PdfFont reportBoldFont() throws IOException {
        return com.itextpdf.kernel.font.PdfFontFactory.createFont("Helvetica-Bold");
    }

    public PdfFont reportRegularFont() throws IOException {
        return com.itextpdf.kernel.font.PdfFontFactory.createFont("Helvetica");
    }

    public PdfFont reportItalicFont() throws IOException {
        return com.itextpdf.kernel.font.PdfFontFactory.createFont("Helvetica-Oblique");
    }
}
