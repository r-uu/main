# ✅ Mehrseitige Rechnungen MIT Template - Vollständig implementiert!

## Was wurde erstellt?

### 1. Java-Implementierung

**`MehrseitigeRechnungGeneratorMitTemplate.java`**
- Kombiniert Template-Design mit mehrseitiger Funktionalität
- Unterstützt automatische Seitenaufteilung
- Zwischensummen und Überträge
- Konfigurierbar (Zeilen pro Seite)

**`BeispielMehrseitigMitTemplate.java`**
- Ausführbares Beispiel mit 60 Positionen auf 4 Seiten
- Verwendet das mehrseitige Template
- Zeigt alle Features

### 2. Template-Datei

**`rechnung-mehrseitig-template.docx`**
- Vorbereitet für bis zu 4 Seiten
- Platzhalter für jede Seite
- Zwischensummen und Überträge eingebaut
- In Word anpassbar (Logo, Farben, Layout)

**`create-mehrseitig-template.sh`**
- Shell-Skript zur Template-Erstellung
- Keine Dependencies nötig (nur bash, zip)

### 3. Dokumentation

Siehe `MEHRSEITIGE-RECHNUNGEN.md` und `MEHRSEITIGE-IMPLEMENTATION.md`

---

## 🚀 Verwendung

### Schritt 1: Template erstellen (einmalig)

```bash
cd app/sandbox.msoffice.word
./create-mehrseitig-template.sh
```

**Ergebnis:** `rechnung-mehrseitig-template.docx` wird erstellt

### Schritt 2: Template anpassen (optional)

Öffnen Sie `rechnung-mehrseitig-template.docx` in Word und:
- Fügen Sie Ihr Logo ein
- Ändern Sie Farben und Schriftarten
- Passen Sie Layout an
- **Lassen Sie die Platzhalter {{...}} unverändert!**

### Schritt 3: Rechnung generieren

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielMehrseitigMitTemplate"
```

**Ergebnis:** `beispiel-mehrseitig-mit-template.docx` mit:
- 60 Positionen auf 4 Seiten
- Template-basiertes Design
- Zwischensummen am Ende jeder Seite
- Überträge am Anfang der Folgeseiten

---

## 📋 Template-Struktur

Das Template unterstützt bis zu **4 Seiten** mit folgenden Platzhaltern:

### Seite 1
```
{{rechnungsnummer}}
{{datum}}
{{empfaenger_firma}}
{{empfaenger_name}}
{{empfaenger_strasse}}
{{empfaenger_plz_ort}}

{{positionen_seite_1}}          ← Tabelle mit Positionen

Übertrag: {{uebertrag_nach_seite_1}}

[SEITENUMBRUCH]
```

### Seite 2
```
Übertrag von Seite 1: {{uebertrag_von_seite_2}}

{{positionen_seite_2}}          ← Tabelle mit Positionen

Übertrag: {{uebertrag_nach_seite_2}}

[SEITENUMBRUCH]
```

### Seite 3
```
Übertrag von Seite 2: {{uebertrag_von_seite_3}}

{{positionen_seite_3}}          ← Tabelle mit Positionen

Übertrag: {{uebertrag_nach_seite_3}}

[SEITENUMBRUCH]
```

### Seite 4 (Letzte)
```
Übertrag von Seite 3: {{uebertrag_von_seite_4}}

{{positionen_seite_4}}          ← Tabelle mit Positionen

Netto-Summe: {{netto_summe}}
MwSt. (19%): {{mwst_summe}}
Gesamt-Summe: {{gesamt_summe}}

{{bemerkungen}}
```

---

## 💻 Im eigenen Code verwenden

```java
// 1. Generator erstellen
MehrseitigeRechnungGeneratorMitTemplate generator = 
    new MehrseitigeRechnungGeneratorMitTemplate("rechnung-mehrseitig-template.docx");

// 2. Konfigurieren
generator.setZeilenProSeite(15);           // 15 Positionen pro Seite
generator.setZwischensummenAnzeigen(true); // Zwischensummen aktiviert

// 3. Rechnung erstellen (mit vielen Positionen)
Rechnung rechnung = ...  // Ihre Rechnung mit z.B. 60 Positionen

// 4. Generieren
generator.generiereRechnung(rechnung, "rechnung.docx");
```

---

## ⚙️ Konfiguration

### Zeilen pro Seite

```java
generator.setZeilenProSeite(15);  // 15 Zeilen pro Seite (Standard)
```

**Wichtig:** Das Template unterstützt 4 Seiten. Bei 15 Zeilen/Seite = max. 60 Positionen.

### Mehr Seiten benötigt?

**Option 1:** Template erweitern
- Öffnen Sie `create-mehrseitig-template.sh`
- Fügen Sie weitere Seiten hinzu (Seite 5, 6, etc.)
- Führen Sie Skript aus

**Option 2:** Programmmatischer Generator verwenden
- Für unbegrenzte Seitenzahl: `MehrseitigeRechnungGenerator` (ohne Template)

---

## 🎨 Vorteile der Template-Variante

| Feature | Mit Template | Ohne Template |
|---------|--------------|---------------|
| **Design anpassbar** | ✅ In Word | ❌ Nur via Code |
| **Logo einfügen** | ✅ Einfach | ⚠️ Programmatisch |
| **Farben ändern** | ✅ Visuell | ⚠️ Via Code |
| **Seitenzahl** | ⚠️ Template-limitiert (4) | ✅ Unbegrenzt |
| **Setup** | ⚠️ Template nötig | ✅ Keine Vorbereitung |

---

## 📊 Vergleich aller Generatoren

### 1. RechnungWordGenerator
- ❌ Kein Template
- ⚠️ Nur 1 Seite
- ✅ Einfach

### 2. RechnungWordGeneratorMitTemplate
- ✅ Mit Template
- ⚠️ Nur 1 Seite
- ✅ Design in Word

### 3. MehrseitigeRechnungGenerator
- ❌ Kein Template
- ✅ Unbegrenzte Seiten
- ✅ Zwischensummen/Überträge

### 4. MehrseitigeRechnungGeneratorMitTemplate ⭐ NEU
- ✅ Mit Template
- ✅ Mehrere Seiten (bis 4)
- ✅ Zwischensummen/Überträge
- ✅ Design in Word

---

## 🎯 Wann welchen Generator verwenden?

### Wenige Positionen (< 20) + einfach
→ `RechnungWordGenerator`

### Wenige Positionen (< 20) + Design wichtig
→ `RechnungWordGeneratorMitTemplate`

### Viele Positionen (20-60) + Design wichtig
→ `MehrseitigeRechnungGeneratorMitTemplate` ⭐ **NEU**

### Sehr viele Positionen (60+) + kein Template nötig
→ `MehrseitigeRechnungGenerator`

---

## 📝 Dateien im Projekt

```
app/sandbox.msoffice.word/
├── create-mehrseitig-template.sh                    ✅ Template-Erstellung
├── rechnung-mehrseitig-template.docx                ✅ Mehrseitiges Template
├── src/main/java/.../
│   ├── MehrseitigeRechnungGeneratorMitTemplate.java ✅ Generator
│   └── BeispielMehrseitigMitTemplate.java           ✅ Beispiel
└── MEHRSEITIGE-RECHNUNGEN-MIT-TEMPLATE.md           ✅ Diese Doku
```

---

## ✅ Status

- ✅ **Generator implementiert** - `MehrseitigeRechnungGeneratorMitTemplate.java`
- ✅ **Beispiel implementiert** - `BeispielMehrseitigMitTemplate.java`
- ✅ **Template erstellt** - `rechnung-mehrseitig-template.docx`
- ✅ **Shell-Skript erstellt** - `create-mehrseitig-template.sh`
- ✅ **Kompiliert erfolgreich** - BUILD SUCCESS
- ✅ **Bereit zur Verwendung**

---

## 🎉 Zusammenfassung

Sie haben jetzt **mehrseitige Rechnungen mit Template-Unterstützung**:

1. ✅ Template in Word anpassbar (Logo, Farben, Layout)
2. ✅ Automatische Seitenumbrüche
3. ✅ Zwischensummen am Seitenende
4. ✅ Überträge auf Folgeseiten
5. ✅ Bis zu 4 Seiten (60 Positionen bei 15/Seite)
6. ✅ Sofort einsatzbereit

**Probieren Sie es aus:**

```bash
cd app/sandbox.msoffice.word
./create-mehrseitig-template.sh
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielMehrseitigMitTemplate"
```

**Das Beste aus beiden Welten: Template-Design + Mehrseitige Funktionalität!** 🎉

