# Word-Dokument Generator mit poi-tl

Sandbox-Modul zur Erstellung von Word-Dokumenten (.docx) mit Apache POI-TL.

## Übersicht

Dieses Modul demonstriert, wie man professionelle Word-Dokumente (Rechnungen) mit Java erstellt.

### Features

- ✅ Rechnungen mit Adresse und Positionen
- ✅ Automatische Berechnung von Summen und MwSt.
- ✅ Tabellen-Darstellung
- ✅ Gut dokumentierter, erweiterbarer Code
- ✅ Unit-Tests

## Schnellstart

### 1. Build

```bash
cd app/sandbox.msoffice.word
mvn clean install
```

### 2. Beispiel ausführen

```bash
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGenerator"
```

Oder in IntelliJ: Rechtsklick auf `BeispielRechnungGenerator` → Run

### 3. Ergebnis

Das Beispiel erstellt eine Datei `beispiel-rechnung.docx` mit:
- Rechnungsnummer und Datum
- Empfänger-Adresse
- 3 Rechnungspositionen in Tabellenform
- Netto-, MwSt.- und Brutto-Summen
- Bemerkungen

## Projektstruktur

```
src/main/java/de/ruu/app/sandbox/msoffice/word/
├── model/
│   ├── Adresse.java                           # Adressdaten
│   ├── Rechnungsposition.java                 # Einzelne Position
│   └── Rechnung.java                          # Komplette Rechnung
├── RechnungWordGenerator.java                 # Word-Generator (ohne Template)
├── RechnungWordGeneratorMitTemplate.java      # Word-Generator (mit Template)
├── BeispielRechnungGenerator.java             # Beispiel ohne Template
└── BeispielRechnungGeneratorMitTemplate.java  # Beispiel mit Template

Zusätzlich:
├── create-minimal-template.sh                 # Shell-Skript für Template
├── TEMPLATE-ANLEITUNG.md                      # Template-Anleitung
└── rechnung-template.docx                     # Word-Template
```

## Verwendung

### Einfaches Beispiel

```java
// 1. Adresse erstellen
Adresse empfaenger = Adresse.builder()
    .firma("Musterfirma GmbH")
    .vorname("Max")
    .nachname("Mustermann")
    .strasse("Musterstraße 123")
    .plz("12345")
    .ort("Musterstadt")
    .build();

// 2. Positionen erstellen
Rechnungsposition pos1 = Rechnungsposition.builder()
    .positionsnummer(1)
    .beschreibung("Beratungsleistung")
    .menge(5.0)
    .einheit("Stunden")
    .einzelpreis(new BigDecimal("80.00"))
    .steuersatz(19.0)
    .build();

// 3. Rechnung zusammenbauen
Rechnung rechnung = Rechnung.builder()
    .rechnungsnummer("R-2026-001")
    .datum(LocalDate.now())
    .empfaenger(empfaenger)
    .positionen(Arrays.asList(pos1))
    .build();

// 4. Word-Dokument generieren
RechnungWordGenerator generator = new RechnungWordGenerator();
generator.generiereRechnung(rechnung, "meine-rechnung.docx");
```

## Erweiterungsmöglichkeiten

### 1. Logo hinzufügen

```java
// In RechnungWordGenerator:
PictureRenderData logo = Pictures.ofLocal("logo.png").size(120, 40).create();
data.put("logo", logo);
```

### 2. Template verwenden

Statt alles programmatisch zu erstellen, können Sie ein Word-Template verwenden:

```java
// template.docx erstellen mit Platzhaltern: {{rechnungsnummer}}, {{datum}}, etc.
XWPFTemplate template = XWPFTemplate.compile("template.docx").render(data);
```

### 3. Styling anpassen

```java
Style style = Style.builder()
    .buildFontSize(12)
    .buildColor("FF0000")
    .buildBold()
    .build();

TextRenderData text = Texts.of("Wichtig!").style(style).create();
```

### 4. Weitere Datentypen

poi-tl unterstützt:
- Bilder: `PictureRenderData`
- Hyperlinks: `HyperlinkTextRenderData`
- Listen: `NumberingRenderData`
- Diagramme: `ChartMultiSeriesRenderData`
- Und vieles mehr...

## Dokumentation

### Klassen-Übersicht

| Klasse | Zweck |
|--------|-------|
| `Adresse` | Adressdaten (Firma, Name, Straße, etc.) |
| `Rechnungsposition` | Einzelne Position mit Preis-Berechnung |
| `Rechnung` | Vollständige Rechnung mit Summen-Berechnung |
| `RechnungWordGenerator` | Erstellt Word-Dokumente |
| `BeispielRechnungGenerator` | Ausführbares Beispiel |

### Wichtige Methoden

**Rechnung:**
- `getNettoSumme()` - Summe ohne MwSt.
- `getMwStSumme()` - Summe der Steuern
- `getGesamtsumme()` - Brutto-Summe

**RechnungWordGenerator:**
- `generiereRechnung(Rechnung, String)` - Erstellt Word-Dokument

## Abhängigkeiten

- **poi-tl 1.12.2** - Template-Engine für Word
- **Apache POI 5.2.5** - Word-Verarbeitung
- **Lombok** - Reduziert Boilerplate-Code
- **JUnit 5** - Tests

## Tests

```bash
mvn test
```

Die Tests prüfen:
- ✅ Dokument-Erstellung
- ✅ Summen-Berechnungen
- ✅ Adress-Formatierung

## Weiterführende Links

- [poi-tl Dokumentation](http://deepoove.com/poi-tl/)
- [Apache POI](https://poi.apache.org/)
- [poi-tl GitHub](https://github.com/Sayi/poi-tl)

## Lizenz

Siehe Projekt-Root

---

**Happy Coding!** 🎉

Weitere Fragen? Siehe Kommentare im Code oder erweitern Sie das Beispiel selbst!

