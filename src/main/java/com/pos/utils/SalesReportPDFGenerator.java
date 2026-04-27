package com.pos.utils;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.pos.models.Sale;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class SalesReportPDFGenerator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private SalesReportPDFGenerator() {
    }

    public static Path generate(String reportTitle, LocalDate fromDate, LocalDate toDate, List<Sale> sales, String companyName) throws Exception {
        String fileName = String.format("sales-report-%s-%s-to-%s.pdf",
                reportTitle.toLowerCase().replace(" ", "-"),
                fromDate.format(DATE_FORMAT),
                toDate.format(DATE_FORMAT));
        Files.createDirectories(Path.of("receipts"));
        return generateTo(Path.of("receipts", fileName), reportTitle, fromDate, toDate, sales, companyName);
    }

    public static Path generateTo(Path outputFile, String reportTitle, LocalDate fromDate, LocalDate toDate, List<Sale> sales, String companyName) throws Exception {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        double total = sales.stream().mapToDouble(Sale::getTotal).sum();
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputFile.toFile()));
        document.open();
        document.add(new Paragraph(companyName));
        document.add(new Paragraph("Sales Report: " + reportTitle));
        document.add(new Paragraph("Date Range: " + fromDate + " to " + toDate));
        document.add(new Paragraph("Number of Sales: " + sales.size()));
        document.add(new Paragraph("Gross Sales Total: " + String.format("%.2f", total)));
        document.add(new Paragraph(" "));

        for (Sale sale : sales) {
            String saleDate = sale.getCreatedAt() == null ? "N/A" : sale.getCreatedAt().toLocalDate().toString();
            document.add(new Paragraph("Sale #" + sale.getId() + " | Date: " + saleDate + " | Total: " + String.format("%.2f", sale.getTotal())));
        }
        if (sales.isEmpty()) {
            document.add(new Paragraph("No sales found for this period."));
        }
        document.close();
        return outputFile.toAbsolutePath();
    }
}
