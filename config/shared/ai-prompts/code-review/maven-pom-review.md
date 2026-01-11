# AI-Prompt: Maven POM Code Review

## Kontext
Code-Review für Maven POM-Dateien im r-uu Multi-Module Projekt.

## Prompt Template

```
Bitte führe ein Code-Review für folgende Maven POM-Datei durch:

[POM XML HIER EINFÜGEN]

Prüfe insbesondere:

1. **Maven Koordinaten**
   - Sind groupId, artifactId und version korrekt?
   - Ist die Version konsistent mit anderen Modulen?

2. **Parent-Child Beziehungen**
   - Ist das Parent korrekt referenziert?
   - Stimmt der relativePath?

3. **Dependency Management**
   - Werden Dependencies aus dem BOM korrekt importiert?
   - Sind Version-Angaben vermieden (sollten aus BOM kommen)?
   - Sind Scopes korrekt gesetzt?

4. **Plugin Konfiguration**
   - Sind Plugins korrekt konfiguriert?
   - Werden Plugin-Versionen aus BOM/Parent übernommen?

5. **Best Practices**
   - Folgt die POM den Maven Best Practices?
   - Ist die Struktur sauber und wartbar?
   - Gibt es unnötige/duplizierte Konfigurationen?

6. **Projekt-spezifisch**
   - Passt die Konfiguration zur r-uu Projekt-Struktur?
   - BOM: Nur dependencyManagement, keine dependencies
   - Root: Parent-Konfiguration für Children
   - Lib/App: Nutzen BOM und Root korrekt

Gib konkrete Verbesserungsvorschläge mit Code-Beispielen.
```

## Beispiel-Verwendung

### BOM Review
```
Ich habe die BOM POM-Datei aktualisiert. Bitte prüfe:
- Ob alle dependency-Versionen aktuell sind
- Ob die Plugin-Versionen kompatibel sind
- Ob nichts Modul-spezifisches enthalten ist

[POM XML]
```

### Root POM Review
```
Ich habe die Root POM konfiguriert. Prüfe bitte:
- Import des BOM
- Konfiguration für Child-Module
- Plugin-Konfiguration für alle Children

[POM XML]
```

### Child Module Review
```
Neues lib-Modul erstellt. Prüfe:
- Parent-Referenz
- Dependency-Deklarationen (ohne Versionen)
- Modul-spezifische Konfiguration

[POM XML]
```

## Checkliste für selbst-Review vor AI

- [ ] Alle `<groupId>`, `<artifactId>`, `<version>` korrekt?
- [ ] Parent korrekt referenziert (falls vorhanden)?
- [ ] Keine hartcodierten Versions in Dependencies?
- [ ] Alle Dependencies tatsächlich benötigt?
- [ ] Properties sinnvoll benannt?
- [ ] Keine Secrets/Passwörter?
- [ ] Kommentare wo nötig?
- [ ] Konsistent mit anderen POMs im Projekt?

