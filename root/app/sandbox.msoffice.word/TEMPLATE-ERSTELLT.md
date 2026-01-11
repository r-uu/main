# ✅ Template-Datei erstellt

## Antwort: Ja, jetzt gibt es eine Template-Datei!

### 📄 Erstellte Datei

**Datei:** `rechnung-template.docx`
**Größe:** 3.2 KB
**Speicherort:** `app/sandbox.msoffice.word/`

### 📋 Enthaltene Platzhalter

Die Template-Datei enthält folgende poi-tl Platzhalter:

- `{{rechnungsnummer}}` - Rechnungsnummer
- `{{datum}}` - Rechnungsdatum
- `{{empfaenger_firma}}` - Firmenname
- `{{empfaenger_name}}` - Vor- und Nachname
- `{{empfaenger_strasse}}` - Straße mit Hausnummer
- `{{empfaenger_plz_ort}}` - PLZ und Ort
- `{{positionen}}` - Tabelle mit Rechnungspositionen
- `{{netto_summe}}` - Netto-Betrag
- `{{mwst_summe}}` - MwSt.-Betrag
- `{{gesamt_summe}}` - Brutto-Gesamtsumme
- `{{bemerkungen}}` - Zusätzliche Bemerkungen

### 🛠️ Erstellung

Erstellt mit: `create-minimal-template.sh`

Das Shell-Skript erstellt ein gültiges Word-Dokument (DOCX) durch:
1. Erstellen der Word-XML-Struktur
2. Einfügen von poi-tl Platzhaltern
3. ZIP-Komprimierung zur .docx-Datei

### 🎨 Template anpassen

Sie können die Template-Datei jetzt in Word (oder LibreOffice) öffnen und anpassen:

```bash
# In Windows:
# Öffnen Sie rechnung-template.docx in Word

# Anpassungen:
# - Logo einfügen (Einfügen → Bilder)
# - Farben ändern
# - Schriftarten anpassen
# - Layout optimieren
# - Eigene Firmendaten eintragen
```

**Wichtig:** Lassen Sie die Platzhalter `{{...}}` unverändert!

### 🚀 Template verwenden

```bash
# Mit Template eine Rechnung generieren:
cd app/sandbox.msoffice.word

mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGeneratorMitTemplate"
```

**Ausgabe:** `beispiel-rechnung-mit-template.docx`

### 📂 Dateien im Modul

```
app/sandbox.msoffice.word/
├── rechnung-template.docx           ✅ NEU - Word-Template
├── create-minimal-template.sh       ✅ NEU - Shell-Skript (ohne Python)
├── create-template.py              (Python-Skript - benötigt python-docx)
├── TEMPLATE-ANLEITUNG.md           (Anleitung)
└── src/...                          (Java-Code)
```

### 🔄 Template neu erstellen

Falls Sie das Template neu erstellen möchten:

```bash
./create-minimal-template.sh
```

Das Shell-Skript benötigt nur Standard-Unix-Tools (bash, zip) - keine zusätzlichen Dependencies!

### ✅ Bereit!

Die Template-Datei ist jetzt vorhanden und kann verwendet werden:

1. ✅ Template existiert: `rechnung-template.docx`
2. ✅ Enthält alle erforderlichen Platzhalter
3. ✅ Kann in Word geöffnet und angepasst werden
4. ✅ Funktioniert mit `BeispielRechnungGeneratorMitTemplate`

**Sie können jetzt sofort Rechnungen mit dem Template generieren!** 🎉

