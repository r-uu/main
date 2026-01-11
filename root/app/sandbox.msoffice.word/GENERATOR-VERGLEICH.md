# Übersicht: Drei Word-Generator-Varianten

## Vergleich der drei Generatoren

| Feature | RechnungWordGenerator | RechnungWordGeneratorMitTemplate | MehrseitigeRechnungGenerator |
|---------|----------------------|----------------------------------|------------------------------|
| **Benötigt Template?** | ❌ Nein | ✅ Ja (`rechnung-template.docx`) | ❌ Nein |
| **Methode** | Programmatisch (POI) | Template + poi-tl | Programmatisch (POI) |
| **Anpassbarkeit** | Nur via Code | Via Word-Template | Nur via Code |
| **Mehrseitig** | ⚠️ Nur 1 Seite | ⚠️ Nur 1 Seite | ✅ Ja, mit Überträgen |
| **Zwischensummen** | ❌ Nein | ❌ Nein | ✅ Ja |
| **Beste für** | Einfache Rechnungen | Design in Word | Viele Positionen |

## 1. RechnungWordGenerator

**Verwendung:**
```bash
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGenerator"
```

**Eigenschaften:**
- ✅ Einfach zu verwenden
- ✅ Kein Template nötig
- ⚠️ Nur für wenige Positionen (< 20)
- ⚠️ Design nur via Code anpassbar

**Ausgabe:** `beispiel-rechnung.docx`

**Code:**
```java
RechnungWordGenerator generator = new RechnungWordGenerator();
generator.generiereRechnung(rechnung, "rechnung.docx");
```

---

## 2. RechnungWordGeneratorMitTemplate

**Vorbereitung:**
```bash
# Template erstellen (einmalig):
./create-minimal-template.sh

# Optional: Template in Word anpassen (Logo, Farben, etc.)
```

**Verwendung:**
```bash
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGeneratorMitTemplate"
```

**Eigenschaften:**
- ✅ Design in Word anpassbar
- ✅ Logo, Farben, Layout visuell editierbar
- ⚠️ Benötigt Template-Datei
- ⚠️ Nur für wenige Positionen (< 20)

**Ausgabe:** `beispiel-rechnung-mit-template.docx`

**Code:**
```java
RechnungWordGeneratorMitTemplate generator = 
    new RechnungWordGeneratorMitTemplate("rechnung-template.docx");
generator.generiereRechnung(rechnung, "rechnung.docx");
```

---

## 3. MehrseitigeRechnungGenerator ⭐ Für viele Positionen

**Verwendung:**
```bash
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielMehrseitigeRechnung"
```

**Eigenschaften:**
- ✅ Kein Template nötig (komplett programmatisch)
- ✅ Automatische Seitenumbrüche
- ✅ Zwischensummen am Seitenende
- ✅ Überträge auf Folgeseiten
- ✅ Konfigurierbar (Zeilen/Seite)
- ✅ Für beliebig viele Positionen (50+)

**Ausgabe:** `beispiel-mehrseitige-rechnung.docx`

**Code:**
```java
MehrseitigeRechnungGenerator generator = new MehrseitigeRechnungGenerator();
generator.setZeilenProSeite(20);           // Anpassbar
generator.setZwischensummenAnzeigen(true); // Optional
generator.generiereRechnung(rechnung, "rechnung.docx");
```

---

## Welchen Generator verwenden?

### Szenario 1: Einfache Rechnung (3-10 Positionen)
→ **`RechnungWordGenerator`**
- Einfach und direkt
- Keine Vorbereitung nötig

### Szenario 2: Design wichtig (Logo, Farben, Layout)
→ **`RechnungWordGeneratorMitTemplate`**
- Template in Word erstellen
- Visuell anpassbar
- Für < 20 Positionen

### Szenario 3: Viele Positionen (20+)
→ **`MehrseitigeRechnungGenerator`** ⭐
- Automatische Seitenumbrüche
- Zwischensummen
- Überträge
- Für beliebig viele Positionen

### Szenario 4: Viele Positionen + individuelles Design
→ Kombinieren:
1. Erstellen Sie ein Template mit `create-minimal-template.sh`
2. Passen Sie Design in Word an
3. Erweitern Sie `MehrseitigeRechnungGenerator` um Template-Support
   (Siehe `RechnungWordGeneratorMitTemplate` als Beispiel)

---

## Templates im Projekt

### Vorhandene Template-Datei

**`rechnung-template.docx`**
- ✅ Bereits vorhanden
- Erstellt mit `create-minimal-template.sh`
- Enthält Platzhalter: `{{rechnungsnummer}}`, `{{datum}}`, etc.
- **Wird verwendet von:** `RechnungWordGeneratorMitTemplate`
- **Wird NICHT verwendet von:** `RechnungWordGenerator`, `MehrseitigeRechnungGenerator`

### Template neu erstellen

```bash
./create-minimal-template.sh
```

Erstellt neue `rechnung-template.docx` mit allen Platzhaltern.

---

## Zusammenfassung

### Kein Template nötig:
- ✅ `RechnungWordGenerator`
- ✅ `MehrseitigeRechnungGenerator`

### Template erforderlich:
- ⚠️ `RechnungWordGeneratorMitTemplate`
  - Template: `rechnung-template.docx`
  - Erstellen mit: `./create-minimal-template.sh`

### Empfehlung

**Für die meisten Anwendungsfälle:**
- Wenige Positionen → `RechnungWordGenerator`
- Viele Positionen → `MehrseitigeRechnungGenerator` ⭐

**Nur wenn Design wichtig:**
- `RechnungWordGeneratorMitTemplate` (+ Template vorbereiten)

---

## Schnelltest

Alle drei Varianten ausprobieren:

```bash
cd app/sandbox.msoffice.word

# 1. Einfach (ohne Template)
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGenerator"

# 2. Mit Template (Template erstellen, falls nicht vorhanden)
./create-minimal-template.sh
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGeneratorMitTemplate"

# 3. Mehrseitig (ohne Template)
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielMehrseitigeRechnung"
```

Alle drei Beispiele erzeugen unterschiedliche .docx-Dateien zum Vergleichen!

