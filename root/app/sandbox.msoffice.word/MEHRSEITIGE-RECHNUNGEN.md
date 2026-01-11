# Mehrseitige Rechnungen mit Überträgen und Zwischensummen

## Problem

Bei Rechnungen mit vielen Positionen (z.B. > 20) kann die Tabelle über mehrere Seiten laufen. 
Professionelle Rechnungen benötigen dann:

- **Seitenumbrüche** nach bestimmter Anzahl Zeilen
- **Zwischensummen** am Ende jeder Seite ("Übertrag")
- **Übertrag-Zeile** am Anfang der Folgeseite
- **Kopfzeile** auf jeder Seite wiederholen

## Lösung 1: Automatischer Seitenumbruch (Word)

### Einfachster Ansatz

Word macht automatisch Seitenumbrüche bei langen Tabellen.

**Vorteil:** Keine Programmierung nötig
**Nachteil:** Keine Zwischensummen

```java
// Normale Tabelle - Word macht automatisch Umbrüche
TableRenderData table = Tables.create(allRows);
```

### Tabellen-Header wiederholen

In Word können Sie die Kopfzeile auf jeder Seite wiederholen lassen:

**In Word-Template:**
1. Tabelle markieren
2. Rechtsklick → Tabelleneigenschaften
3. Tab "Zeile" → ✓ "Überschriften wiederholen"

**Programmatisch (Apache POI):**
```java
XWPFTable table = document.createTable();
XWPFTableRow headerRow = table.getRow(0);
headerRow.getCtRow().getTrPr().addNewTblHeader();  // Header wiederholen
```

## Lösung 2: Manuelle Seitenumbrüche mit Zwischensummen

### Konzept

```
Seite 1:
┌─────────────────────────────────┐
│ Pos. | Beschreibung | Betrag   │
├─────────────────────────────────┤
│  1   | Artikel 1    | 100,00 € │
│  2   | Artikel 2    | 200,00 € │
│ ...  | ...          | ...      │
│ 20   | Artikel 20   | 500,00 € │
├─────────────────────────────────┤
│      | Übertrag     | 2.500,00€│ ← Zwischensumme
└─────────────────────────────────┘

[SEITENUMBRUCH]

Seite 2:
┌─────────────────────────────────┐
│      | Übertrag     | 2.500,00€│ ← Übertrag von vorheriger Seite
├─────────────────────────────────┤
│ 21   | Artikel 21   | 300,00 € │
│ ...  | ...          | ...      │
└─────────────────────────────────┘
```

### Implementierung

```java
public class MehrseitigeRechnungGenerator {
    
    private static final int ZEILEN_PRO_SEITE = 20;
    
    public void generiereRechnungMitSeitenumbruechen(
        Rechnung rechnung, 
        String ausgabeDatei
    ) throws IOException {
        
        XWPFDocument document = new XWPFDocument();
        
        List<Rechnungsposition> positionen = rechnung.getPositionen();
        
        // Positionen in Seiten aufteilen
        List<List<Rechnungsposition>> seiten = teileInSeiten(positionen);
        
        BigDecimal uebertrag = BigDecimal.ZERO;
        
        for (int seiteNr = 0; seiteNr < seiten.size(); seiteNr++) {
            List<Rechnungsposition> seitenPositionen = seiten.get(seiteNr);
            
            // Übertrag von vorheriger Seite (außer erste Seite)
            if (seiteNr > 0) {
                fuegeUebertragEin(document, uebertrag);
            }
            
            // Tabelle für diese Seite
            XWPFTable table = erstelleTabelleSeite(
                document, 
                seitenPositionen,
                seiteNr == 0  // Header nur auf erster Seite
            );
            
            // Zwischensumme berechnen
            BigDecimal seitenSumme = berechneSeitenSumme(seitenPositionen);
            uebertrag = uebertrag.add(seitenSumme);
            
            // Zwischensumme/Übertrag einfügen (außer letzte Seite)
            if (seiteNr < seiten.size() - 1) {
                fuegeZwischensummeEin(table, uebertrag, true); // "Übertrag"
                
                // Seitenumbruch
                document.createParagraph().createRun().addBreak(BreakType.PAGE);
            }
        }
        
        // Endsumme
        fuegeEndsummeEin(document, rechnung);
        
        // Speichern
        try (FileOutputStream out = new FileOutputStream(ausgabeDatei)) {
            document.write(out);
        }
    }
    
    private List<List<Rechnungsposition>> teileInSeiten(
        List<Rechnungsposition> positionen
    ) {
        List<List<Rechnungsposition>> seiten = new ArrayList<>();
        
        for (int i = 0; i < positionen.size(); i += ZEILEN_PRO_SEITE) {
            int end = Math.min(i + ZEILEN_PRO_SEITE, positionen.size());
            seiten.add(positionen.subList(i, end));
        }
        
        return seiten;
    }
    
    private void fuegeUebertragEin(XWPFDocument document, BigDecimal betrag) {
        XWPFTable table = document.createTable();
        XWPFTableRow row = table.getRow(0);
        
        row.getCell(0).setText("Übertrag von vorheriger Seite:");
        row.addNewTableCell().setText(formatiereBetrag(betrag));
        
        // Fett formatieren
        row.getCell(0).getParagraphs().get(0).getRuns().get(0).setBold(true);
    }
    
    private void fuegeZwischensummeEin(
        XWPFTable table, 
        BigDecimal betrag,
        boolean istUebertrag
    ) {
        XWPFTableRow row = table.createRow();
        
        // Leere Zellen für Pos., Beschreibung, etc.
        row.getCell(0).setText("");
        row.getCell(1).setText(istUebertrag ? "Übertrag" : "Zwischensumme");
        // ... weitere Zellen ...
        
        // Letzte Zelle: Betrag (fett)
        int lastCell = row.getTableCells().size() - 1;
        XWPFTableCell cell = row.getCell(lastCell);
        cell.setText(formatiereBetrag(betrag));
        cell.getParagraphs().get(0).getRuns().get(0).setBold(true);
    }
    
    private BigDecimal berechneSeitenSumme(List<Rechnungsposition> positionen) {
        return positionen.stream()
            .map(Rechnungsposition::getGesamtpreis)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

## Lösung 3: Mit Template und poi-tl

### Template-Ansatz

**Template-Datei** (`rechnung-mehrseitig-template.docx`):

```
Seite 1:
{{#positionen_seite_1}}
Tabelle mit Positionen
{{/positionen_seite_1}}

Übertrag: {{uebertrag_seite_1}}

[SEITENUMBRUCH]

Seite 2:
Übertrag: {{uebertrag_seite_1}}

{{#positionen_seite_2}}
Tabelle mit Positionen
{{/positionen_seite_2}}
```

**Java-Code:**

```java
Map<String, Object> data = new HashMap<>();

// Seite 1
data.put("positionen_seite_1", erstelleTabellenDaten(positionen.subList(0, 20)));
data.put("uebertrag_seite_1", formatiereBetrag(zwischensumme1));

// Seite 2
data.put("positionen_seite_2", erstelleTabellenDaten(positionen.subList(20, positionen.size())));

XWPFTemplate.compile("rechnung-mehrseitig-template.docx").render(data);
```

**Nachteil:** Anzahl Seiten muss bekannt sein (oder Template dynamisch erstellen).

## Lösung 4: PDF-Generierung (Alternative)

Für komplexe mehrseitige Dokumente ist **PDF-Generierung** oft besser geeignet:

### Mit iText oder Apache PDFBox

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>8.0.3</version>
</dependency>
```

```java
Document document = new Document(new PdfDocument(new PdfWriter(output)));

// Tabelle mit automatischen Seitenumbrüchen
Table table = new Table(6);
table.setKeepTogether(false); // Seitenumbrüche erlauben

// Header auf jeder Seite
table.setHeaderTopBorder(new SolidBorder(1));
table.setSkipFirstHeader(false);

// Positionen hinzufügen
for (Rechnungsposition pos : positionen) {
    table.addCell(pos.getPositionsnummer().toString());
    table.addCell(pos.getBeschreibung());
    // ... weitere Zellen
}

document.add(table);
```

**Vorteile:**
- ✅ Automatische Seitenumbrüche
- ✅ Header-Wiederholung eingebaut
- ✅ Bessere Kontrolle über Layout
- ✅ PDF ist finales Format (nicht editierbar)

**Nachteil:**
- Kunde kann Rechnung nicht in Word bearbeiten

## Empfehlung

### Für wenige Positionen (< 30):
→ **Lösung 1**: Normale Tabelle, Word macht automatisch Umbrüche

### Für viele Positionen MIT Zwischensummen:
→ **Lösung 2**: Manuelle Seitenumbrüche programmieren

### Für sehr komplexe Rechnungen:
→ **Lösung 4**: PDF-Generierung mit iText

## Beispiel-Implementierung

Ich kann eine vollständige Beispiel-Klasse `MehrseitigeRechnungGenerator.java` erstellen, die:

1. Positionen automatisch auf Seiten aufteilt
2. Zwischensummen/Überträge einfügt
3. Seitenumbrüche macht
4. Kopfzeilen wiederholt

Soll ich das implementieren?

## Weitere Überlegungen

### Steuerung der Zeilenzahl

```java
// Dynamisch basierend auf Beschreibungslänge
int zeilenProSeite = berechneOptimaleZeilenzahl(positionen);

// Oder fix
private static final int ZEILEN_PRO_SEITE = 25;
```

### Fußnoten auf jeder Seite

```java
// Am Ende jeder Seite vor Seitenumbruch
XWPFParagraph footer = document.createParagraph();
footer.createRun().setText("Fortsetzung auf nächster Seite...");
footer.setPageBreak(true);
```

### Seiten-Nummerierung

```java
// In Template oder programmatisch
"Seite 1 von 3"
"Seite 2 von 3"
etc.
```

---

**Möchten Sie, dass ich eine vollständige Implementierung erstelle?**

