#!/bin/bash
# Erstellt eine minimale Word-Template-Datei ohne Python-Abhängigkeiten
TEMPLATE_FILE="rechnung-template.docx"
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"
# Minimale Word-Struktur erstellen
mkdir -p _rels
mkdir -p word/_rels
mkdir -p docProps
# [Content_Types].xml
cat > '[Content_Types].xml' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
<Default Extension="xml" ContentType="application/xml"/>
<Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
<Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
<Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>
EOF
# _rels/.rels
cat > '_rels/.rels' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
<Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
<Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>
EOF
# word/_rels/document.xml.rels
cat > 'word/_rels/document.xml.rels' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
</Relationships>
EOF
# word/document.xml mit Platzhaltern
cat > 'word/document.xml' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
<w:body>
<!-- Rechnungsnummer und Datum (rechtsbündig) -->
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Rechnungsnummer: </w:t></w:r>
<w:r><w:t>{{rechnungsnummer}}</w:t></w:r>
</w:p>
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Datum: </w:t></w:r>
<w:r><w:t>{{datum}}</w:t></w:r>
</w:p>
<!-- Leerzeile -->
<w:p/>
<!-- Empfänger-Adresse -->
<w:p><w:r><w:t>{{empfaenger_firma}}</w:t></w:r></w:p>
<w:p><w:r><w:t>{{empfaenger_name}}</w:t></w:r></w:p>
<w:p><w:r><w:t>{{empfaenger_strasse}}</w:t></w:r></w:p>
<w:p><w:r><w:t>{{empfaenger_plz_ort}}</w:t></w:r></w:p>
<!-- Leerzeile -->
<w:p/>
<!-- Betreff -->
<w:p>
<w:r><w:rPr><w:b/><w:sz w:val="32"/></w:rPr><w:t>Rechnung</w:t></w:r>
</w:p>
<!-- Leerzeile -->
<w:p/>
<!-- Anrede -->
<w:p><w:r><w:t>Sehr geehrte Damen und Herren,</w:t></w:r></w:p>
<w:p/>
<w:p><w:r><w:t>für unsere erbrachten Leistungen erlauben wir uns, wie folgt abzurechnen:</w:t></w:r></w:p>
<w:p/>
<!-- Tabellen-Platzhalter -->
<w:p><w:r><w:t>{{positionen}}</w:t></w:r></w:p>
<!-- Leerzeile -->
<w:p/>
<!-- Summen -->
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:t>Netto-Summe: {{netto_summe}}</w:t></w:r>
</w:p>
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:t>MwSt. (19%): {{mwst_summe}}</w:t></w:r>
</w:p>
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Gesamt-Summe: {{gesamt_summe}}</w:t></w:r>
</w:p>
<!-- Leerzeile -->
<w:p/>
<!-- Bemerkungen -->
<w:p><w:r><w:t>{{bemerkungen}}</w:t></w:r></w:p>
<!-- Leerzeile -->
<w:p/>
<!-- Schluss -->
<w:p><w:r><w:t>Mit freundlichen Grüßen</w:t></w:r></w:p>
<w:p/>
<w:p/>
<w:p><w:r><w:t>Ihre Firma GmbH</w:t></w:r></w:p>
</w:body>
</w:document>
EOF
# docProps/core.xml
cat > 'docProps/core.xml' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<dc:creator>Template Generator</dc:creator>
<dc:title>Rechnung Template</dc:title>
</cp:coreProperties>
EOF
# docProps/app.xml
cat > 'docProps/app.xml' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties">
<Application>poi-tl Template</Application>
</Properties>
EOF
# ZIP zu DOCX
zip -q -r "$TEMPLATE_FILE" '[Content_Types].xml' _rels docProps word
# Zurück zum ursprünglichen Verzeichnis
cd - > /dev/null
# Template verschieben
mv "$TEMP_DIR/$TEMPLATE_FILE" .
# Aufräumen
rm -rf "$TEMP_DIR"
echo "✅ Template erstellt: $TEMPLATE_FILE"
echo ""
echo "Enthaltene Platzhalter:"
echo "  - {{rechnungsnummer}}"
echo "  - {{datum}}"
echo "  - {{empfaenger_firma}}"
echo "  - {{empfaenger_name}}"
echo "  - {{empfaenger_strasse}}"
echo "  - {{empfaenger_plz_ort}}"
echo "  - {{positionen}}"
echo "  - {{netto_summe}}"
echo "  - {{mwst_summe}}"
echo "  - {{gesamt_summe}}"
echo "  - {{bemerkungen}}"
echo ""
echo "Sie können die Datei jetzt in Word öffnen und anpassen!"
