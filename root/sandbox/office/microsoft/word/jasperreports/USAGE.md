# JasperReports Service - Verwendung

## Konzept

Du kannst **beliebige JRXML-Templates** im `templates/` Verzeichnis ablegen und von deiner Java-App Reports generieren lassen.

```
┌─────────────────────────────────────┐
│ Deine Java-Anwendung                │
│ (GraalVM 25 + JPMS)                 │
│                                     │
│  InvoiceData data = ...             │
│  Path pdf = client.generateInvoice  │
│             (data);                 │
│         ↓                           │
└─────────────────────────────────────┘
         │ HTTP
         ↓
┌─────────────────────────────────────┐
│ Docker Container                    │
│ (Java 17, kein JPMS)                │
│                                     │
│  templates/invoice.jrxml            │
│  → JasperReports kompiliert         │
│  → Füllt mit InvoiceData            │
│  → Exportiert als PDF/DOCX          │
│         ↓                           │
└─────────────────────────────────────┘
         │
         ↓
    output/xxx.pdf

```

## ✅ Ja, genau so funktioniert es!

1. **Templates**: Beliebige `.jrxml` Dateien in `templates/` ablegen
2. **Daten**: Java-Objekt erstellen (z.B. `InvoiceData`)
3. **Generieren**: `client.generateInvoice(data)` aufrufen
4. **Fertig**: PDF/DOCX liegt in `output/`

## Schnellstart

### 1. Service starten

```bash
jasper-start
```

### 2. Java-Client verwenden

```java
// Client erstellen
var client = new JasperReportsClient();

// Daten erstellen (Builder Pattern - kein manuelles JSON!)
InvoiceData invoice = InvoiceData.builder()
    .invoiceNumber("INV-2026-001")
    .invoiceDate(LocalDate.now())
    .customer("Max Mustermann", "Hauptstr. 1, 12345 Berlin")
    .addItem("Beratung", 10, 100.0)
    .addItem("Entwicklung", 20, 120.0)
    .notes("Zahlbar in 30 Tagen")
    .build();

// Report generieren - KEIN JSON-Encoding nötig!
Path pdf = client.generateInvoice(invoice);

System.out.println("PDF: " + pdf);
```

### 3. Beispiel ausführen

```bash
cd root/sandbox/office/microsoft/word/jasperreports
mvn exec:java -Dexec.mainClass="de.ruu.jasper.example.InvoiceExample"
```

## Eigene Templates verwenden

### Template erstellen

1. Erstelle `templates/mein-report.jrxml`
2. Definiere Parameter:
   ```xml
   <parameter name="title" class="java.lang.String"/>
   <parameter name="amount" class="java.lang.Double"/>
   ```

### Von Java verwenden

```java
// Eigenes Datenobjekt
class MyReportData {
    String title;
    Double amount;
    // ... getter/setter
}

// Report generieren
MyReportData data = new MyReportData();
data.title = "Mein Report";
data.amount = 1234.56;

Path pdf = client.generateReport("mein-report.jrxml", data, ReportFormat.PDF);
```

## Verfügbare Templates

- **invoice.jrxml** - Rechnungsvorlage mit InvoiceData
- **test.jrxml** - Einfacher Test-Report

## Java-Client API

### Konstruktor

```java
// Standard (localhost:8090)
var client = new JasperReportsClient();

// Custom URL
var client = new JasperReportsClient("http://other-host:8090");
```

### Rechnung generieren

```java
// PDF (Standard)
Path pdf = client.generateInvoice(invoiceData);

// DOCX
Path docx = client.generateInvoice(invoiceData, ReportFormat.DOCX);
```

### Beliebiger Report

```java
Path pdf = client.generateReport(
    "mein-template.jrxml",  // Template-Name
    dataObject,              // Dein Datenobjekt
    ReportFormat.PDF         // Format
);
```

### Service-Check

```java
if (client.isServiceAvailable()) {
    // Service läuft
}
```

## InvoiceData Builder

```java
InvoiceData invoice = InvoiceData.builder()
    .invoiceNumber("INV-001")           // Rechnungsnummer
    .invoiceDate(LocalDate.now())       // Rechnungsdatum
    .dueDate(LocalDate.now().plusDays(30))  // Fälligkeitsdatum
    .customer("Name", "Adresse")        // Kunde
    .addItem("Beschreibung", 10, 100.0) // Position hinzufügen
    .addItem("Weitere Position", 5, 50.0)
    .notes("Hinweise")                  // Optionale Notizen
    .build();

// Automatische Berechnungen
double subtotal = invoice.getSubtotal();  // Summe aller Positionen
double tax = invoice.getTax();            // 19% MwSt
double total = invoice.getTotal();        // Gesamtbetrag
```

## Vorteile

✅ **Kein manuelles JSON** - Client serialisiert automatisch  
✅ **Type-Safe** - Java-Objekte statt String-Manipulation  
✅ **Builder Pattern** - Lesbare, fluent API  
✅ **Automatische Berechnungen** - MwSt, Summen, etc.  
✅ **Beliebige Templates** - Einfach .jrxml in templates/ legen  
✅ **Isoliert** - Läuft in Docker, keine Konflikte mit GraalVM  

## Ausgabe-Formate

- **PDF** - Standard, universell einsetzbar
- **DOCX** - Microsoft Word Format

## Pfade

- **Templates**: `root/sandbox/office/microsoft/word/jasperreports/templates/`
- **Output**: `root/sandbox/office/microsoft/word/jasperreports/output/`
- **Java-Client**: Im `client` Package nutzbar

## Troubleshooting

**Service nicht erreichbar:**
```bash
jasper-status       # Status prüfen
jasper-start        # Service starten
jasper-logs         # Logs ansehen
```

**Template nicht gefunden:**
```bash
# Prüfe ob Template existiert
ls root/sandbox/office/microsoft/word/jasperreports/templates/

# Container-Templates prüfen
jasper-shell
ls /app/templates
```

**Generierter Report nicht gefunden:**
```bash
# Output-Verzeichnis prüfen
ls root/sandbox/office/microsoft/word/jasperreports/output/
```

## Weitere Beispiele

Siehe: `src/main/java/de/ruu/jasper/example/InvoiceExample.java`

