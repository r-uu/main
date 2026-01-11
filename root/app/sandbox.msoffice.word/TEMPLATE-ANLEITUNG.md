# Word-Template Anleitung

## Schnellstart: Template automatisch erstellen

```bash
cd app/sandbox.msoffice.word
./create-minimal-template.sh
```

Dies erstellt `rechnung-template.docx` mit allen Platzhaltern.

**Benötigt nur:** bash, zip (Standard-Unix-Tools)

---

## Manuelle Template-Erstellung

Falls Sie das Template manuell in Word erstellen möchten:

### 1. Neues Word-Dokument erstellen

Öffnen Sie Microsoft Word und erstellen Sie ein neues leeres Dokument.

### 2. Platzhalter einfügen

poi-tl verwendet **doppelte geschweifte Klammern** als Platzhalter: `{{platzhalter}}`

### 3. Template-Struktur

Erstellen Sie folgende Struktur:

```
Ihre Firma GmbH · Musterstraße 1 · 12345 Musterstadt
[klein, grau, für Fensterumschlag]

[2 Leerzeilen]

{{empfaenger_firma}}
{{empfaenger_name}}
{{empfaenger_strasse}}
{{empfaenger_plz_ort}}

[2 Leerzeilen]

Rechnungsnummer: {{rechnungsnummer}}     [rechtsbündig]
Datum: {{datum}}                          [rechtsbündig]

[Leerzeile]

Rechnung                                  [fett, 16pt]

[Leerzeile]

Sehr geehrte Damen und Herren,

für unsere erbrachten Leistungen erlauben wir uns, wie folgt abzurechnen:

[Leerzeile]

{{positionen}}
[Dieser Platzhalter wird durch eine Tabelle ersetzt]

[Leerzeile]

                    Netto-Summe:  {{netto_summe}}    [rechtsbündig]
                    MwSt. (19%):  {{mwst_summe}}     [rechtsbündig]
                 Gesamt-Summe:  {{gesamt_summe}}  [rechtsbündig, fett]

[Leerzeile]

{{bemerkungen}}

[Leerzeile]

Mit freundlichen Grüßen

[2 Leerzeilen]

Ihre Firma GmbH
```

### 4. Verfügbare Platzhalter

| Platzhalter | Beschreibung | Typ |
|-------------|--------------|-----|
| `{{rechnungsnummer}}` | Rechnungsnummer | Text |
| `{{datum}}` | Rechnungsdatum (dd.MM.yyyy) | Text |
| `{{empfaenger_firma}}` | Firmenname | Text |
| `{{empfaenger_name}}` | Vor- und Nachname | Text |
| `{{empfaenger_strasse}}` | Straße mit Hausnummer | Text |
| `{{empfaenger_plz_ort}}` | PLZ und Ort | Text |
| `{{positionen}}` | Tabelle mit Positionen | Tabelle |
| `{{netto_summe}}` | Netto-Summe formatiert | Text |
| `{{mwst_summe}}` | MwSt.-Betrag formatiert | Text |
| `{{gesamt_summe}}` | Brutto-Summe formatiert | Text |
| `{{bemerkungen}}` | Zusätzliche Bemerkungen | Text |

### 5. Besonderheit: Tabellen-Platzhalter

Für `{{positionen}}`:
- Einfach den Platzhalter in eine eigene Zeile schreiben
- poi-tl ersetzt ihn automatisch durch eine Tabelle
- Die Tabelle enthält: Pos., Beschreibung, Menge, Einheit, Einzelpreis, Gesamtpreis

### 6. Speichern

- Dateiname: `rechnung-template.docx`
- Format: Word-Dokument (.docx)
- Speicherort: `app/sandbox.msoffice.word/`

### 7. Template testen

```bash
# Generator mit Template ausführen
mvn exec:java -Dexec.mainClass="de.ruu.app.sandbox.msoffice.word.BeispielRechnungGeneratorMitTemplate"
```

---

## Template anpassen

### Logo hinzufügen

1. Fügen Sie ein Bild in das Template ein (z.B. oben rechts)
2. Das Logo bleibt im generierten Dokument erhalten

### Farben ändern

1. Markieren Sie Text im Template
2. Wählen Sie Farbe in Word
3. Die Formatierung wird übernommen

### Schriftarten ändern

1. Markieren Sie Text im Template
2. Wählen Sie Schriftart und -größe
3. Die Formatierung wird übernommen

### Weitere Platzhalter hinzufügen

1. Fügen Sie `{{neuer_platzhalter}}` im Template ein
2. Erweitern Sie `RechnungWordGenerator.erstelleDatenModell()`:
   ```java
   data.put("neuer_platzhalter", "Wert");
   ```

---

## Fehlerbehebung

### Platzhalter werden nicht ersetzt

- ✅ Prüfen Sie die Schreibweise: `{{platzhalter}}` (doppelte Klammern!)
- ✅ Keine Leerzeichen: `{{ platzhalter }}` funktioniert NICHT
- ✅ Template-Pfad korrekt in Java: `XWPFTemplate.compile("rechnung-template.docx")`

### Tabelle wird nicht eingefügt

- ✅ Platzhalter `{{positionen}}` muss in eigener Zeile stehen
- ✅ Keine zusätzliche Formatierung um den Platzhalter

### Word-Datei kann nicht geöffnet werden

- ✅ Stellen Sie sicher, dass die Datei nicht bereits geöffnet ist
- ✅ Prüfen Sie Schreibrechte im Ausgabe-Verzeichnis

---

## Beispiel-Template herunterladen

Nach Ausführung von `create-template.py` können Sie das Template:

1. In Word öffnen
2. Anpassen (Logo, Farben, Texte)
3. Speichern
4. Mit dem Generator verwenden

Das Template ist sofort einsatzbereit und kann beliebig angepasst werden!

