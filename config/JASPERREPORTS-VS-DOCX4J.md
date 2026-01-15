# ✅ JasperReports - FUNKTIONIERT JETZT!

**Datum:** 2026-01-15  
**Status:** ✅ **GELÖST - JasperReports läuft mit Groovy-Compiler!**

---

## 🎉 Problem gelöst!

**Ursprüngliches Problem:**
```
JRException: Errors were encountered when compiling report expressions class file
  error: cannot find symbol
  symbol: class JREvaluator
  error: package net.sf.jasperreports.engine does not exist
```

**Ursache:**
- JasperReports verwendete den Eclipse JDT-Compiler (ECJ)
- Dieser konnte die JasperReports-Klassen nicht im Classpath finden
- JPMS-Modul-System blockierte Zugriff auf interne Packages

**Lösung:**
1. ✅ Groovy als Expression Language verwenden
2. ✅ Groovy-Compiler-Dependencies hinzufügen
3. ✅ Template mit `language="groovy"` konfigurieren

---

## 📦 Hinzugefügte Dependencies

```xml
<!-- Groovy für JasperReports Expression Language -->
<dependency>
    <groupId>org.apache.groovy</groupId>
    <artifactId>groovy</artifactId>
    <version>4.0.24</version>
</dependency>
<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports-groovy</artifactId>
    <version>7.0.1</version>
</dependency>
```

---

## 🚀 Verwendung

### Terminal
```bash
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/jasper
mvn exec:java -Dexec.mainClass="de.ruu.sandbox.office.microsoft.word.jasper.InvoiceGenerator"
```

### IntelliJ
**Run Configuration vorhanden:**
- Name: `InvoiceGenerator [DEBUG]` (Application)
- Oder: `jasper InvoiceGenerator [exec:java]` (Maven)

**Verwendung:**
1. Run → Debug 'InvoiceGenerator [DEBUG]'
2. Oder: Maven → jasper → Plugins → exec → exec:java

---

## 📊 Output

**Generierte Dateien:**
- `target/rechnung_beispiel.docx` (DOCX-Format)
- `target/rechnung_beispiel.pdf` (PDF-Format)

**Features:**
- ✅ Mehrseitige Rechnungen (50 Positionen)
- ✅ Automatische Zwischensummen pro Seite
- ✅ Gesamtsumme am Ende
- ✅ Professionelles Layout
- ✅ JPMS-kompatibel mit Groovy

---

## 🔧 Template-Konfiguration

**Wichtig im JRXML:**
```xml
<jasperReport 
    language="groovy"    <!-- WICHTIG: Groovy statt Java! -->
    ...>
```

**Expression-Beispiele:**
```groovy
<!-- Variablen -->
<variableExpression><![CDATA[new BigDecimal(0)]]></variableExpression>

<!-- Textfelder -->
<textFieldExpression><![CDATA[$P{INVOICE_NUMBER}]]></textFieldExpression>
<textFieldExpression><![CDATA[$P{INVOICE_DATE}.toString()]]></textFieldExpression>
```

---

## 📚 Vergleich: Groovy vs. Java Expressions

| Aspekt | Java (ECJ) | Groovy |
|--------|------------|--------|
| **Funktioniert unter JPMS** | ❌ Nein | ✅ Ja |
| **Classpath-Zugriff** | ❌ Probleme | ✅ Funktioniert |
| **Syntax** | Java | Groovy (Java-kompatibel) |
| **Performance** | Schneller | Etwas langsamer |
| **Setup** | Komplex | Einfach |

---

## 🎯 Beide Lösungen funktionieren jetzt!

### Option 1: JasperReports (mit Groovy) ✅
**Vorteile:**
- ✅ Visual Designer (Jaspersoft Studio)
- ✅ Automatische Zwischensummen
- ✅ Professionelle Templates
- ✅ PDF + DOCX Export
- ✅ Weniger Code

**Nachteile:**
- ❌ Groovy-Dependency nötig
- ❌ Externe IDE für Design
- ❌ Komplexere Setup

**Ideal für:** Wiederkehrende Reports mit festem Layout

### Option 2: docx4j ✅
**Vorteile:**
- ✅ Pure Java
- ✅ Volle Kontrolle
- ✅ Keine externen Tools
- ✅ Einfaches Setup
- ✅ Flexibel

**Nachteile:**
- ❌ Manuelle Berechnungen
- ❌ Mehr Code
- ❌ Kein visueller Designer

**Ideal für:** Dynamische Dokumente mit komplexer Logik

---

## 💡 Empfehlung

**Für Ihr Projekt:**

1. **Einfache Rechnungen mit festem Layout:**
   → **JasperReports** verwenden
   
2. **Dynamische Dokumente mit komplexer Logik:**
   → **docx4j** verwenden

**Beide Lösungen sind produktionsreif! ✅**

---

## 🔍 Troubleshooting

### Problem: "cannot find symbol JREvaluator"
**Lösung:** Groovy-Dependencies hinzugefügt (siehe oben)

### Problem: "Template not found"
**Lösung:** Template liegt in `src/main/resources/templates/`

### Problem: "Unable to load report" (XML-Parsing)
**Lösung:** War ein früheres Problem - jetzt gelöst mit Groovy

---

## 📁 Dateien

```
root/sandbox/office/microsoft/word/jasper/
├── pom.xml                              ← Groovy-Dependencies
├── src/main/
│   ├── java/
│   │   └── de/ruu/sandbox/.../
│   │       ├── InvoiceGenerator.java   ← Main-Klasse
│   │       ├── InvoiceData.java        ← Datenmodell
│   │       └── InvoiceItem.java
│   └── resources/
│       ├── templates/
│       │   └── invoice_template.jrxml  ← Template (Groovy!)
│       └── logback.xml
└── target/
    ├── rechnung_beispiel.docx          ← Output DOCX
    └── rechnung_beispiel.pdf           ← Output PDF
```

---

## ✅ Status: BEIDE LÖSUNGEN FUNKTIONIEREN!

- ✅ **JasperReports:** Funktioniert mit Groovy-Compiler
- ✅ **docx4j:** Funktioniert mit Java 25 + JPMS

**Wählen Sie die Lösung, die am besten zu Ihrem Use Case passt!** 🎉

---

**Nächste Schritte:**

1. ✅ Testen Sie beide Lösungen
2. ✅ Entscheiden Sie sich für eine
3. ✅ Passen Sie das Template an Ihre Bedürfnisse an
4. ✅ Integrieren Sie in Ihre Anwendung

**Viel Erfolg! 🚀**
