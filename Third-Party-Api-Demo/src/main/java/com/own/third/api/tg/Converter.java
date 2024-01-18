package com.own.third.api.tg;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Converter {

    public static void main(String[] args) throws IOException {
        // Generate Markdown Table
        final Table.Builder tableBuilder = new Table.Builder()
                .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_CENTER)
                .withRowLimit(2)
                .addRow("Date", "START", "END", "BTC", "USDT");
        tableBuilder.addRow("2024-01-11", "05:55:47", "06:55:47", "-0.00321", "+124.46713429");
        String text = tableBuilder.build().toString();

        // Convert Markdown to HTML
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(parser.parse(text));

        // Convert HTML to image
        try {
            String pdfPath = "temp.pdf";
            String pngPath = "output.png";

            convertHtmlToPdf(html, pdfPath);
            convertPdfToPng(pdfPath, pngPath);

            // Optionally, delete the PDF file if it's no longer needed
//            new File(pdfPath).delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void convertHtmlToPdf(String html, String pdfPath) throws IOException {
        try (OutputStream os = new FileOutputStream(pdfPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
        }
    }

    public static void convertPdfToPng(String pdfPath, String pngPath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 300); // Render the first page with 300 DPI
            ImageIO.write(image, "PNG", new File(pngPath));
        }
    }

}
