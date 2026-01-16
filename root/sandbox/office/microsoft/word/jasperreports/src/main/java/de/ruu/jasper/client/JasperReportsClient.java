package de.ruu.jasper.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Eleganter Java-Client für JasperReports Service
 *
 * Verwendung:
 * <pre>
 * var client = new JasperReportsClient();
 *
 * var invoice = InvoiceData.builder()
 *     .invoiceNumber("INV-2026-001")
 *     .invoiceDate(LocalDate.now())
 *     .customer("Max Mustermann", "Hauptstr. 1, 12345 Berlin")
 *     .addItem("Beratung", 10, 100.0)
 *     .build();
 *
 * Path pdf = client.generateInvoice(invoice);
 * </pre>
 */
public class JasperReportsClient {

    private static final String DEFAULT_SERVICE_URL = "http://localhost:8090";
    private static final String GENERATE_ENDPOINT = "/api/report/generate";

    private final String serviceUrl;
    private final HttpClient httpClient;
    private final Gson gson;

    public JasperReportsClient() {
        this(DEFAULT_SERVICE_URL);
    }

    public JasperReportsClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                (json, typeOfT, context) -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .setPrettyPrinting()
            .create();
    }

    /**
     * Generiert eine Rechnung als PDF
     *
     * @param invoiceData Die Rechnungsdaten
     * @return Pfad zur generierten PDF-Datei
     */
    public Path generateInvoice(Object invoiceData) throws IOException, InterruptedException {
        return generateReport("invoice.jrxml", invoiceData, ReportFormat.PDF);
    }

    /**
     * Generiert eine Rechnung im gewünschten Format
     */
    public Path generateInvoice(Object invoiceData, ReportFormat format) throws IOException, InterruptedException {
        return generateReport("invoice.jrxml", invoiceData, format);
    }

    /**
     * Generiert einen Report mit beliebigem Template
     *
     * @param template Name des Templates (z.B. "invoice.jrxml")
     * @param data Daten-Objekt (wird automatisch zu JSON konvertiert)
     * @param format Ausgabeformat (PDF oder DOCX)
     * @return Pfad zur generierten Datei
     */
    public Path generateReport(String template, Object data, ReportFormat format)
            throws IOException, InterruptedException {

        // Request-Body erstellen
        var requestBody = new ReportRequest();
        requestBody.template = template;
        requestBody.format = format.name().toLowerCase();
        requestBody.data = data;

        String jsonBody = gson.toJson(requestBody);

        // HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(serviceUrl + GENERATE_ENDPOINT))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        // Request senden
        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        // Response verarbeiten
        if (response.statusCode() != 200) {
            throw new IOException("Report generation failed: " + response.body());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> result = gson.fromJson(response.body(), Map.class);

        if (!(Boolean) result.get("success")) {
            throw new IOException("Report generation failed: " + result.get("error"));
        }

        String outputFile = (String) result.get("outputFile");

        // Konvertiere Docker-Pfad zu Host-Pfad
        // /app/output/xxx.pdf -> root/sandbox/office/microsoft/word/jasperreports/output/xxx.pdf
        String fileName = outputFile.substring(outputFile.lastIndexOf('/') + 1);
        Path localPath = Paths.get("root/sandbox/office/microsoft/word/jasperreports/output", fileName);

        // Warte kurz bis Datei verfügbar ist
        int retries = 10;
        while (retries-- > 0 && !Files.exists(localPath)) {
            Thread.sleep(100);
        }

        if (!Files.exists(localPath)) {
            throw new IOException("Generated file not found: " + localPath);
        }

        return localPath;
    }

    /**
     * Prüft ob der Service verfügbar ist
     */
    public boolean isServiceAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl + "/health"))
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Report-Request Model (intern)
     */
    private static class ReportRequest {
        String template;
        String format;
        Object data;
    }

    /**
     * Ausgabeformat
     */
    public enum ReportFormat {
        PDF,
        DOCX
    }
}

