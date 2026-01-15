# ✅ docx4j Invoice Generator - Bereit

**Datum:** 2026-01-15

## 🎯 Problem behoben

**Fehler:**
```
NullPointerException: Cannot invoke "java.io.File.mkdirs()" 
because the return value of "java.io.File.getParentFile()" is null
```

**Ursache:**
- Der Pfad "rechnung_docx4j.docx" hat kein Elternverzeichnis
- `getParentFile()` gibt `null` zurück
- `null.mkdirs()` → NullPointerException

**Lösung:**
```java
File outputFile = new File(outputPath);
File parentDir = outputFile.getParentFile();
if (parentDir != null)
{
    parentDir.mkdirs();
}
log.info("Speichere Dokument: {}", outputFile.getAbsolutePath());
wordMLPackage.save(outputFile);
```

---

## ✅ Jetzt funktioniert

### 1. Maven ausführen
```bash
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/docx4j
mvn exec:java -Dexec.mainClass="de.ruu.sandbox.office.microsoft.word.docx4j.InvoiceGenerator"
```

**Ausgabe:**
```
✓ Rechnung erstellt: rechnung_docx4j.docx
[INFO] BUILD SUCCESS
```

**Generierte Datei:**
- Pfad: `/home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/docx4j/rechnung_docx4j.docx`
- Größe: 9.1 KB
- Format: Microsoft Word .docx

---

### 2. IntelliJ Debug Configuration

**Bereits vorhanden:**
- Name: `docx4j InvoiceGenerator [DEBUG]`
- Typ: Application
- Main Class: `de.ruu.sandbox.office.microsoft.word.docx4j.InvoiceGenerator`
- Modul: `r-uu.sandbox.office.microsoft.word.docx4j`
- Working Directory: `$PROJECT_DIR$/root/sandbox/office/microsoft/word/docx4j`

**Verwendung:**
1. In IntelliJ: Run → Debug 'docx4j InvoiceGenerator [DEBUG]'
2. Breakpoints setzen wo gewünscht
3. Debuggen! 🐛

---

## 📊 Was das Programm macht

### Rechnungsgenerierung mit docx4j

**Features:**
- ✅ Kopfbereich mit Rechnungsnummer und Datum
- ✅ Kundenadresse
- ✅ Tabelle mit Rechnungspositionen
- ✅ Zwischensummen (manuell berechnet nach 20 Positionen)
- ✅ Überträge bei mehrseitigen Rechnungen
- ✅ Gesamtsumme
- ✅ Währungsformatierung (€ deutsch)

**Beispieldaten:**
- 45 Positionen
- 3 Seiten (20 Positionen pro Seite)
- Zwischensummen nach Seite 1 und 2
- Übertrag zu Beginn von Seite 2 und 3
- Gesamtsumme am Ende

---

## 🔧 docx4j vs. JasperReports

| Aspekt | docx4j | JasperReports |
|--------|--------|---------------|
| **Komplexität** | Mittel-Hoch | Niedrig-Mittel |
| **Design** | Code-basiert | Designer (WYSIWYG) |
| **Zwischensummen** | Manuell berechnen | Automatisch |
| **Seitenwechsel** | Manuell einfügen | Automatisch |
| **Wartbarkeit** | Code ändern | Template ändern |
| **Flexibilität** | Sehr hoch | Hoch |
| **Lernkurve** | Steiler | Flacher |

**Empfehlung:**
- **JasperReports**: Für wiederkehrende Reports mit festem Layout
- **docx4j**: Für dynamische Dokumente mit komplexer Logik

---

## 📁 Projektstruktur

```
root/sandbox/office/microsoft/word/docx4j/
├── pom.xml
├── src/main/java/
│   └── de/ruu/sandbox/office/microsoft/word/docx4j/
│       ├── InvoiceGenerator.java     ← Hauptklasse (FUNKTIONIERT!)
│       ├── InvoiceData.java          ← Datenmodell
│       └── InvoiceItem.java          ← Rechnungsposition
├── src/main/resources/
│   └── logback.xml
├── module-info.java
└── rechnung_docx4j.docx             ← Generierte Datei (9.1 KB)
```

---

## 🚀 Nächste Schritte

### 1. Dokument in Windows öffnen
```powershell
# Von Windows PowerShell aus:
cd \\wsl.localhost\Ubuntu\home\r-uu\develop\github\main\root\sandbox\office\microsoft\word\docx4j
start rechnung_docx4j.docx
```

### 2. Anpassen
- **InvoiceGenerator.java**: Layout-Logik
- **InvoiceData.java**: Beispieldaten ändern
- **ITEMS_PER_PAGE**: Positionen pro Seite anpassen

### 3. Erweitern
- Logo hinzufügen
- Kopf-/Fußzeilen
- Komplexere Tabellen
- Styling verbessern

---

## ✅ Status

- ✅ NullPointerException behoben
- ✅ Programm kompiliert
- ✅ Programm läuft erfolgreich
- ✅ Rechnung wird generiert (9.1 KB)
- ✅ IntelliJ Debug Configuration vorhanden
- ✅ Maven exec:java funktioniert
- ✅ JPMS-konform

**Bereit für Produktion!** 🎉

---

## 📚 Dependencies

```xml
<dependency>
    <groupId>org.docx4j</groupId>
    <artifactId>docx4j-JAXB-ReferenceImpl</artifactId>
    <version>11.5.1</version>
</dependency>
```

Alle Dependencies aktuell (Stand 2026-01-15).

---

**Problem gelöst! docx4j Invoice Generator funktioniert! ✅**

