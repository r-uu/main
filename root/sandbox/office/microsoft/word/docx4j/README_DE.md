# Word-Dokumentgenerierung mit docx4j

Dieses Modul bietet eine produktionsreife Lösung zur Generierung von Word-Rechnungen (.docx) mit Java 25.

## Schnellstart

```bash
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/docx4j
mvn clean compile exec:java
```

**Ergebnis:** `rechnung_docx4j.docx`

## Hauptmerkmale

- ✅ Mehrseitige Rechnungen mit automatischen Seitenumbrüchen
- ✅ Automatische Seitenberechnung basierend auf Seitendimensionen
- ✅ Zwischensummen am Ende jeder Seite
- ✅ Gesamtsumme am Dokumentende
- ✅ Deutsche Währungs- und Datumsformatierung
- ✅ Java 25 kompatibel (mit JPMS)

## Architektur

### Klassen

- **InvoiceGenerator.java** - Haupt-Generator mit automatischer Seitenberechnung (~24 Zeilen/Seite)
- **InvoiceData.java** - Rechnungsdaten-Container
- **InvoiceItem.java** - Einzelne Rechnungsposition

## Automatische Seitenberechnung

Die Advanced-Version berechnet die maximale Anzahl von Zeilen pro Seite automatisch:

```
Seitenhöhe (A4): 297mm = 15840 Twips
- Oberer Rand: 1440 Twips (25mm)
- Unterer Rand: 1440 Twips (25mm)  
- Kopfbereich: 2880 Twips (~50mm)
- Fußbereich: 1440 Twips (~25mm)
= Verfügbar: 8640 Twips

Zeilenhöhe: 360 Twips (~6.3mm)

→ Max. Zeilen pro Seite: 24
```

## Konfiguration

Passen Sie die Konstanten in `InvoiceGenerator.java` an:

```java
private static final int PAGE_HEIGHT_TWIPS = 15840;
private static final int TOP_MARGIN_TWIPS = 1440;
private static final int ROW_HEIGHT_TWIPS = 360;
```

## Technologie

- docx4j 11.4.9
- Java 25 (Oracle GraalVM)
- JPMS-aktiviert

Siehe [README.md](README.md) für ausführliche Dokumentation (Englisch).

---

**Stand:** 15.01.2026 | **Status:** ✅ Produktionsreif

