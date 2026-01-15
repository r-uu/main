# Word Invoice Generation with docx4j

✅ **Production-ready solution for generating Word invoices (.docx) with Java 25**

## Quick Start

### Maven Command Line
```bash
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/docx4j
mvn clean compile exec:java
```

### IntelliJ IDEA
Use Run Configuration: **"docx4j InvoiceGenerator [Application]"**

### Result
Generated invoice: `rechnung_docx4j.docx`

---

## Features

- ✅ **Multi-page invoices** with automatic page breaks
- ✅ **Automatic page calculation** based on page dimensions
- ✅ **Subtotals** at end of each page  
- ✅ **Grand total** at document end
- ✅ **Formatted tables** with headers
- ✅ **German formatting** (currency, dates)
- ✅ **Java 25 compatible** (JPMS-enabled)

---

## Architecture

### Core Classes

**InvoiceGenerator.java**
- Main generator with automatic page calculation
- Calculates maximum rows based on page dimensions
- Configurable margins, header/footer heights
- Production-ready implementation (~24 rows per page)

**InvoiceData.java**
- Invoice data container
- Customer information
- Invoice metadata (number, date)
- Item list management

**InvoiceItem.java**
- Single invoice position
- Description, quantity, unit price
- Automatic total calculation

---

## Usage Example

```java
import de.ruu.sandbox.office.microsoft.word.docx4j.*;

// Create invoice data
InvoiceData invoice = new InvoiceData();
invoice.setInvoiceNumber("RE-2026-001");
invoice.setInvoiceDate(LocalDate.now());
invoice.setCustomerName("Example Corp");
invoice.setCustomerAddress("Example Street 123\n12345 Example City");

// Add items
invoice.addItem(new InvoiceItem("Consulting Service", 10, 150.00));
invoice.addItem(new InvoiceItem("Development", 40, 120.00));

// Generate invoice
new InvoiceGenerator().generateInvoice(invoice, "my_invoice.docx");
```

---

## Configuration

### Automatic Page Calculation

The generator automatically calculates maximum items per page based on dimensions:

```java
// Page dimensions in Twips (1/1440 inch)
private static final int PAGE_HEIGHT_TWIPS = 15840;  // A4: 297mm
private static final int TOP_MARGIN_TWIPS = 1440;     // 1 inch
private static final int BOTTOM_MARGIN_TWIPS = 1440;  // 1 inch
private static final int HEADER_HEIGHT_TWIPS = 2880;  // ~5cm
private static final int FOOTER_HEIGHT_TWIPS = 1440;  // ~2.5cm
private static final int ROW_HEIGHT_TWIPS = 360;      // ~6.3mm

// Automatically calculated:
// MAX_ITEMS_PER_PAGE = (PAGE_HEIGHT - MARGINS - HEADER - FOOTER) / ROW_HEIGHT
// Result: ~24 rows per page
```

Adjust these constants in `InvoiceGenerator.java` to customize page layout.

---

## Technology Stack

- **docx4j 11.4.9**: Word document generation
- **SLF4J 2.0.16**: Logging facade
- **Log4j2 2.25.3**: Logging implementation
- **Java 25**: Oracle GraalVM with JPMS support

---

## Advanced Features

### Template-Based Generation

docx4j can also use existing .docx files as templates:

```java
// Load template
WordprocessingMLPackage template = 
    WordprocessingMLPackage.load(new File("template.docx"));

// Replace placeholders
MainDocumentPart mainDoc = template.getMainDocumentPart();
mainDoc.variableReplace(Map.of(
    "CUSTOMER_NAME", "John Doe",
    "INVOICE_NUMBER", "RE-2026-001"
));

// Save
template.save(new File("output.docx"));
```

### PDF Export

Export directly to PDF (requires additional dependencies):

```java
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;

// Convert to PDF
FOSettings foSettings = Docx4J.createFOSettings();
foSettings.setWmlPackage(wordMLPackage);
Docx4J.toFO(foSettings, new FileOutputStream("invoice.pdf"), Docx4J.FLAG_EXPORT_PREFER_XSL);
```

---

## JPMS Configuration

Module descriptor (`module-info.java`):

```java
module de.ruu.sandbox.office.microsoft.word.docx4j {
    // docx4j dependencies
    requires org.docx4j.openxml_objects;
    requires org.docx4j.core;
    requires jakarta.xml.bind;
    
    // Logging
    requires org.slf4j;
    
    // Java modules
    requires java.xml;
    
    // Open for JAXB reflection
    opens de.ruu.sandbox.office.microsoft.word.docx4j to jakarta.xml.bind;
}
```

---

## Troubleshooting

### Document not created
- Check write permissions in target directory
- Review logs for error messages

### Formatting issues
- docx4j uses Twips (1/1440 inch) for measurements
- Use conversion: `millimeters * 56.7 = Twips`

### OutOfMemoryError with large documents
- Increase heap: `-Xmx2g`
- Use streaming for very large documents

---

## Resources

- [docx4j Documentation](https://www.docx4java.org/)
- [docx4j GitHub Examples](https://github.com/plutext/docx4j)
- [Office Open XML Spec](https://www.ecma-international.org/publications-and-standards/standards/ecma-376/)

---

## License

docx4j is licensed under Apache License 2.0.

---

**Last Updated:** 2026-01-15  
**Java Version:** Oracle GraalVM 25  
**docx4j Version:** 11.4.9  
**Status:** ✅ Production Ready

