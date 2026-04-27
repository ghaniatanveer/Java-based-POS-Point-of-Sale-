package com.pos.utils;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.pos.models.Sale;
import com.pos.models.SaleItem;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PDFReceiptGenerator {
    private PDFReceiptGenerator() {
    }

    public static Path generate(Sale sale, String companyName, String footer) throws Exception {
        Files.createDirectories(Path.of("receipts"));
        Path file = Path.of("receipts", "receipt-" + sale.getId() + ".pdf");
        return generateTo(file, sale, companyName, footer);
    }

    public static Path generateTo(Path outputFile, Sale sale, String companyName, String footer) throws Exception {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputFile.toFile()));
        document.open();
        document.add(new Paragraph(companyName));
        document.add(new Paragraph("Receipt #" + sale.getId()));
        document.add(new Paragraph(" "));
        for (SaleItem item : sale.getItems()) {
            document.add(new Paragraph(item.getProductName() + " x" + item.getQuantity() + " = " + String.format("%.2f", item.getLineTotal())));
        }
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Subtotal: " + String.format("%.2f", sale.getSubtotal())));
        document.add(new Paragraph("Discount: " + String.format("%.2f", sale.getDiscount())));
        document.add(new Paragraph("Tax: " + String.format("%.2f", sale.getTax())));
        document.add(new Paragraph("Total: " + String.format("%.2f", sale.getTotal())));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(footer));
        document.close();
        return outputFile.toAbsolutePath();
    }
}
