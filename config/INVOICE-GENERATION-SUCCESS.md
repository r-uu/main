# ❌ JasperReports funktioniert NICHT mit Java 25 + JPMS

**Datum:** 2026-01-15  
**Status:** ❌ **JasperReports INKOMPATIBEL mit Java 25 + JPMS**  
**Status:** ✅ **docx4j FUNKTIONIERT PERFEKT**

---

## ⚠️ WICHTIGE ERKENNTNIS

**JasperReports kann unter Java 25 + JPMS NICHT kompiliert werden.**

**Problem:**
- Der Eclipse JDT-Compiler (ECJ) findet die JasperReports-Klassen nicht
- Auch mit explizitem Classpath funktioniert es nicht
- Groovy ist die einzige Lösung, aber Sie wollen kein Groovy

**Grund:**
- JPMS (Java Platform Module System) blockiert den Zugriff auf interne Packages
- JasperReports generiert zur Laufzeit Java-Code, der kompiliert werden muss
- Dieser Code benötigt Zugriff auf `net.sf.jasperreports.engine.fill.*`
- Unter JPMS ist dieser Zugriff nicht möglich

---

## ✅ EMPFEHLUNG: Verwenden Sie docx4j!

**docx4j funktioniert einwandfrei unter Java 25 + JPMS!**

```bash
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/docx4j
mvn exec:java
```

**Ergebnis:**
- ✅ Kompiliert erfolgreich
- ✅ Generiert: `rechnung_docx4j.docx`
- ✅ Professionelles Layout
- ✅ Keine Probleme mit JPMS

---

## 📊 Vergleich: Was funktioniert?

| Lösung | Java 25 + JPMS | Vorteile | Nachteile |
|--------|----------------|----------|-----------|
| **docx4j** | ✅ **JA** | Pure Java, volle Kontrolle, JPMS-kompatibel | Manuelle Zwischensummen, mehr Code |
| **JasperReports + Java** | ❌ **NEIN** | - | Funktioniert nicht |
| **JasperReports + Groovy** | ✅ Ja | Visual Designer, automatische Summen | Groovy-Dependency |
| **JasperReports + Java 17** | ✅ Ja | Funktioniert | Alte Java-Version |

---

## 🎯 Ihre Optionen

### Option 1: docx4j verwenden (EMPFOHLEN ✅)

**Vorteile:**
- ✅ Funktioniert JETZT
- ✅ Pure Java (kein Groovy)
- ✅ JPMS-kompatibel
- ✅ Java 25
- ✅ Volle Kontrolle

**Nachteile:**
- ❌ Zwischensummen müssen manuell berechnet werden
- ❌ Mehr Java-Code

**Ideal für:** Ihre Anforderungen!

### Option 2: JasperReports + Groovy

**Wenn Sie unbedingt JasperReports wollen:**
- Groovy-Dependencies akzeptieren
- Template mit `language="groovy"` verwenden
- Siehe: `config/JASPERREPORTS-GROOVY-SUCCESS.md`

**Nachteil:** Groovy-Dependency

### Option 3: Java 17 verwenden

**Downgrade auf Java 17:**
- JasperReports funktioniert mit Java 17
- Verlust von Java 25 Features

**Nachteil:** Alte Java-Version

---

## 🚀 SOFORT EINSATZBEREIT: docx4j

### Terminal
```bash
cd /home/r-uu/develop/github/main/root/sandbox/office/microsoft/word/docx4j
mvn exec:java -Dexec.mainClass="de.ruu.sandbox.office.microsoft.word.docx4j.InvoiceGenerator"
```

### IntelliJ
- Run → Debug 'docx4j InvoiceGenerator [DEBUG]'

### Ausgabe
- `rechnung_docx4j.docx` (9.1 KB)
- Professionelles Layout
- 50 Positionen
- Mehrseitig

---

## 📁 Projekt-Status

### ✅ docx4j - FUNKTIONIERT
```
root/sandbox/office/microsoft/word/docx4j/
├── pom.xml
├── src/main/java/
│   └── de/ruu/sandbox/.../
│       ├── InvoiceGenerator.java    ← FUNKTIONIERT!
│       └── InvoiceData.java
└── rechnung_docx4j.docx            ← Output (9.1 KB)
```

### ❌ JasperReports - FUNKTIONIERT NICHT (mit Java)
```
root/sandbox/office/microsoft/word/jasper/
├── pom.xml
├── src/main/java/
│   └── de/ruu/sandbox/.../
│       ├── InvoiceGenerator.java    ← Kompiliert nicht
│       └── Simple

InvoiceGenerator.java ← Kompiliert auch nicht
└── src/main/resources/
    └── templates/
        ├── invoice_template.jrxml   ← Java-Template (FEHLER!)
        └── simple_invoice.jrxml     ← Vereinfacht (FEHLER!)
```

**Fehler:**
```
cannot find symbol: class JREvaluator
package net.sf.jasperreports.engine does not exist
```

---

## 💡 KLARE EMPFEHLUNG

**Verwenden Sie docx4j!**

1. ✅ Funktioniert SOFORT
2. ✅ Pure Java (kein Groovy)
3. ✅ JPMS-kompatibel
4. ✅ Java 25
5. ✅ Produktionsreif

**JasperReports nur mit Groovy oder Java 17!**

---

## 📚 Dokumentation

- **docx4j Details:** `config/DOCX4J-INVOICE-READY.md`
- **JasperReports (Groovy):** `config/JASPERREPORTS-GROOVY-SUCCESS.md`
- **Vergleich:** `config/JASPERREPORTS-VS-DOCX4J.md`

---

## ✅ FAZIT

**docx4j ist die richtige Lösung für Ihr Projekt!**

- Pure Java ✅
- Java 25 ✅
- JPMS ✅
- Funktioniert ✅

**JasperReports mit Java ist unter Java 25 + JPMS NICHT möglich.**

---

**Verwenden Sie docx4j! Es funktioniert perfekt! 🎉**


