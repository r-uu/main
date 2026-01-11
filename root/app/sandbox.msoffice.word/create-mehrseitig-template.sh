#!/bin/bash
# ============================================================================
# Mehrseitiges Template erstellen
# ============================================================================
TEMPLATE_FILE="rechnung-mehrseitig-template.docx"
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"
mkdir -p _rels word/_rels docProps
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
# word/document.xml - Mehrseitiges Template
cat > 'word/document.xml' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
<w:body>
<!-- KOPFBEREICH (nur Seite 1) -->
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
<w:p/>
<w:p><w:r><w:t>{{empfaenger_firma}}</w:t></w:r></w:p>
<w:p><w:r><w:t>{{empfaenger_name}}</w:t></w:r></w:p>
<w:p><w:r><w:t>{{empfaenger_strasse}}</w:t></w:r></w:p>
<w:p><w:r><w:t>{{empfaenger_plz_ort}}</w:t></w:r></w:p>
<w:p/>
<w:p>
<w:r><w:rPr><w:b/><w:sz w:val="32"/></w:rPr><w:t>Rechnung</w:t></w:r>
</w:p>
<w:p/>
<!-- SEITE 1 -->
<w:p><w:r><w:t>{{positionen_seite_1}}</w:t></w:r></w:p>
<w:p/>
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Übertrag: {{uebertrag_nach_seite_1}}</w:t></w:r>
</w:p>
<!-- SEITENUMBRUCH -->
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
<!-- SEITE 2 -->
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Übertrag von Seite 1: {{uebertrag_von_seite_2}}</w:t></w:r>
</w:p>
<w:p/>
<w:p><w:r><w:t>{{positionen_seite_2}}</w:t></w:r></w:p>
<w:p/>
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Übertrag: {{uebertrag_nach_seite_2}}</w:t></w:r>
</w:p>
<!-- SEITENUMBRUCH -->
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
<!-- SEITE 3 -->
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Übertrag von Seite 2: {{uebertrag_von_seite_3}}</w:t></w:r>
</w:p>
<w:p/>
<w:p><w:r><w:t>{{positionen_seite_3}}</w:t></w:r></w:p>
<w:p/>
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Übertrag: {{uebertrag_nach_seite_3}}</w:t></w:r>
</w:p>
<!-- SEITENUMBRUCH -->
<w:p><w:r><w:br w:type="page"/></w:r></w:p>
<!-- SEITE 4 (Letzte) -->
<w:p>
<w:pPr><w:jc w:val="right"/></w:pPr>
<w:r><w:rPr><w:b/></w:rPr><w:t>Übertrag von Seite 3: {{uebertrag_von_seite_4}}</w:t></w:r>
</w:p>
<w:p/>
<w:p><w:r><w:t>{{positionen_seite_4}}</w:t></w:r></w:p>
<!-- ENDSUMMEN -->
<w:p/>
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
<w:p/>
<w:p><w:r><w:t>{{bemerkungen}}</w:t></w:r></w:p>
<w:p/>
<w:p><w:r><w:t>Mit freundlichen Grüßen</w:t></w:r></w:p>
</w:body>
</w:document>
EOF
# docProps/core.xml
cat > 'docProps/core.xml' << 'EOF'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/">
<dc:creator>Template Generator</dc:creator>
<dc:title>Mehrseitige Rechnung Template</dc:title>
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
cd - > /dev/null
mv "$TEMP_DIR/$TEMPLATE_FILE" .
rm -rf "$TEMP_DIR"
echo "✅ Mehrseitiges Template erstellt: $TEMPLATE_FILE"
echo ""
echo "Unterstützt bis zu 4 Seiten mit folgenden Platzhaltern:"
echo "  Seite 1: {{positionen_seite_1}}, {{uebertrag_nach_seite_1}}"
echo "  Seite 2: {{uebertrag_von_seite_2}}, {{positionen_seite_2}}, {{uebertrag_nach_seite_2}}"
echo "  Seite 3: {{uebertrag_von_seite_3}}, {{positionen_seite_3}}, {{uebertrag_nach_seite_3}}"
echo "  Seite 4: {{uebertrag_von_seite_4}}, {{positionen_seite_4}}"
echo "  Endsummen: {{netto_summe}}, {{mwst_summe}}, {{gesamt_summe}}"
