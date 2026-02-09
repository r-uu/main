# IntelliJ Plugin Fix - Schnellanleitung

## Was wurde geändert?

Die IntelliJ IDEA Konfiguration (`.idea/compiler.xml`) wurde aktualisiert, um das fehlende `lombok-mapstruct-binding` hinzuzufügen.

## Was müssen Sie jetzt tun?

### Schritt 1: IntelliJ IDEA neu starten

**Wichtig:** Ein einfacher Neustart reicht nicht aus!

1. In IntelliJ IDEA öffnen: **File** → **Invalidate Caches...**
2. Im Dialog **alle Optionen** ankreuzen:
   - ✅ Invalidate and Restart
   - ✅ Clear file system cache and Local History
   - ✅ Clear VCS Log caches and indexes
   - ✅ Clear downloaded shared indexes
3. Klicken Sie auf **Invalidate and Restart**

### Schritt 2: Nach dem Neustart

1. Warten Sie, bis IntelliJ das Projekt vollständig geladen hat
2. Öffnen Sie das **Maven Tool Window** (rechts in der Seitenleiste)
3. Klicken Sie auf **Reload All Maven Projects** (kreisförmiger Pfeil oben links)
4. Warten Sie, bis der Import abgeschlossen ist

### Schritt 3: Projekt neu bauen

1. **Build** → **Rebuild Project**
2. Warten Sie, bis der Build abgeschlossen ist

### Schritt 4: Überprüfen

Die Plugin-Fehler sollten jetzt verschwunden sein! Überprüfen Sie:
- Öffnen Sie eine Klasse mit `@Mapper` oder `@Data` Annotations
- Es sollten keine roten Fehlerunterstriche mehr vorhanden sein
- Die Auto-Completion sollte funktionieren

## Wenn es immer noch Fehler gibt

Falls immer noch Fehler angezeigt werden:

1. Schließen Sie IntelliJ IDEA komplett
2. Löschen Sie das `.idea` Verzeichnis:
   ```bash
   cd /home/r-uu/develop/github/main
   rm -rf .idea
   ```
3. Öffnen Sie das Projekt neu in IntelliJ
4. Warten Sie, bis IntelliJ das Projekt neu indiziert hat
5. Wiederholen Sie Schritt 2-3 von oben

**Hinweis:** Diese drastische Maßnahme sollte nur als letzter Ausweg verwendet werden, da Sie alle IntelliJ-Projekteinstellungen verlieren.

## Technische Details

Siehe: [INTELLIJ-PLUGIN-FIX.md](INTELLIJ-PLUGIN-FIX.md)

---
**Status:** ✅ Bereit zum Testen
**Erstellt:** 2026-02-09

