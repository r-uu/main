package de.ruu.jasper.example;

import de.ruu.jasper.client.JasperReportsClient;
import de.ruu.jasper.client.JasperReportsClient.ReportFormat;
import de.ruu.jasper.model.InvoiceData;

import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Beispiel: Wie man JasperReports Client verwendet
 */
public class InvoiceExample {

    public static void main(String[] args) throws Exception {

        // 1. Client erstellen
        var client = new JasperReportsClient();

        // Service-Verfügbarkeit prüfen
        if (!client.isServiceAvailable()) {
            System.err.println("❌ JasperReports Service ist nicht erreichbar!");
            System.err.println("   Starte den Service mit: jasper-start");
            System.exit(1);
        }

        // 2. Rechnungsdaten erstellen (mit Builder Pattern)
        InvoiceData invoice = InvoiceData.builder()
            .invoiceNumber("INV-2026-001")
            .invoiceDate(LocalDate.now())
            .dueDate(LocalDate.now().plusDays(30))
            .customer("Max Mustermann", "Hauptstraße 1\n12345 Berlin")
            .addItem("Softwareentwicklung", 10, 100.00)
            .addItem("Beratung", 5, 150.00)
            .addItem("Schulung", 2, 200.00)
            .notes("Zahlbar innerhalb 30 Tagen.\nVielen Dank für Ihren Auftrag!")
            .build();

        System.out.println("📄 Generiere Rechnung...");
        System.out.println("   Nummer: " + invoice.getInvoiceNumber());
        System.out.println("   Kunde: " + invoice.getCustomerName());
        System.out.println("   Zwischensumme: " + invoice.getSubtotal() + " €");
        System.out.println("   MwSt (19%): " + invoice.getTax() + " €");
        System.out.println("   Gesamt: " + invoice.getTotal() + " €");
        System.out.println();

        // 3. PDF generieren
        Path pdf = client.generateInvoice(invoice);
        System.out.println("✅ PDF erstellt: " + pdf);

        // 4. Alternativ: DOCX generieren
        Path docx = client.generateInvoice(invoice, ReportFormat.DOCX);
        System.out.println("✅ DOCX erstellt: " + docx);

        System.out.println();
        System.out.println("🎉 Fertig! Du kannst die Dateien jetzt öffnen:");
        System.out.println("   " + pdf.toAbsolutePath());
        System.out.println("   " + docx.toAbsolutePath());
    }
}

