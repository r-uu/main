# 🚀 DashAppRunner aus IntelliJ starten - SCHNELLANLEITUNG

## ✅ LÖSUNG: VM Options eintragen

### Schritt 1: Run Configuration öffnen
1. In IntelliJ: **Run** → **Edit Configurations...**
2. Wähle **DashAppRunner** aus (oder **DashAppRunner_WORKING**)

### Schritt 2: VM Options einfügen

Kopiere diese Zeile in das **VM options** Feld:

```
--add-modules jakarta.annotation,jakarta.inject,org.slf4j --add-reads de.ruu.app.jeeeraaah.frontend.ui.fx=ALL-UNNAMED -Dglass.gtk.uiScale=1.5
```

**⚠️ WICHTIG:** Alle drei Module müssen dabei sein: `jakarta.annotation`, `jakarta.inject` UND `org.slf4j`!

### Schritt 3: Apply & Run

Klicke **Apply**, dann **OK**, dann **Run** ▶️

---

## 📋 Vollständige Configuration

Stelle sicher, dass folgende Werte gesetzt sind:

| Feld | Wert |
|------|------|
| **Name** | DashAppRunner |
| **Main class** | `de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner` |
| **VM options** | `--add-modules jakarta.annotation,jakarta.inject,org.slf4j --add-reads de.ruu.app.jeeeraaah.frontend.ui.fx=ALL-UNNAMED -Dglass.gtk.uiScale=1.5` |
| **Use classpath of module** | `r-uu.app.jeeeraaah.frontend.ui.fx` |

---

## 🔍 Warum ist das notwendig?

Mit **Java 25 JPMS** (Java Platform Module System) müssen Module explizit hinzugefügt werden:

- `--add-modules` → Macht Jakarta und SLF4J Module verfügbar
- `--add-reads` → Erlaubt dem Frontend-Modul, CDI/Weld (vom Classpath) zu nutzen

**Gestern hat es funktioniert, weil diese VM Options bereits in der Run Configuration waren.**
Sie wurden vermutlich versehentlich entfernt oder die Configuration wurde zurückgesetzt.

---

## 🆘 Troubleshooting

### Fehler: "Module org.slf4j not found"
→ VM Options fehlen! Siehe Schritt 2 oben.

### Fehler: "Module jakarta.annotation not found"
→ VM Options fehlen! Siehe Schritt 2 oben.

### Configuration existiert nicht
→ Erstelle neue **Application** Configuration:
1. Run → Edit Configurations → **+** → **Application**
2. Name: `DashAppRunner`
3. Main class: `de.ruu.app.jeeeraaah.frontend.ui.fx.dash.DashAppRunner`
4. VM options: siehe Schritt 2
5. Module: `r-uu.app.jeeeraaah.frontend.ui.fx`

### IntelliJ zeigt die Configuration nicht an
→ IntelliJ neu starten. Die `.idea/runConfigurations/DashAppRunner_WORKING.xml` sollte automatisch erkannt werden.

---

## ✨ Fertig!

Nach dem Eintragen der VM Options kannst du DashAppRunner wie gewohnt mit dem grünen **Run**-Button starten.

**Debugging** funktioniert dann auch normal mit dem **Debug**-Button 🐞
