# InvoiceExample in IntelliJ ausführen

## Problem
Beim Ausführen über Maven (`exec:exec`) erhalten Sie den Fehler:
```
Could not make working directory: '/wsl.localhost/Ubuntu/...'
```

## Lösung 1: Application Run Configuration verwenden (Empfohlen)

Eine **Application Run Configuration** wurde bereits erstellt: `InvoiceExample`

### So verwenden Sie sie:

1. Öffne **Run** → **Edit Configurations** (oder Dropdown oben rechts)
2. Wähle **InvoiceExample** aus der Liste
3. Klicke auf **Run** (grüner Pfeil) oder **Debug**

### Vorteile:
- ✅ Kein Working Directory Problem
- ✅ Direktes Debugging möglich
- ✅ Schneller als Maven exec
- ✅ IntelliJ-native Ausführung

## Lösung 2: Maven exec:java verwenden

Wenn Sie dennoch Maven verwenden möchten:

```bash
cd root/sandbox/office/microsoft/word/jasperreports
mvn exec:java
```

**WICHTIG:** Verwenden Sie `exec:java` statt `exec:exec`!

## Lösung 3: Build Delegation deaktivieren (für Maven Goals)

Falls Sie Maven Goals direkt aus IntelliJ ausführen möchten:

1. **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Maven** → **Runner**
2. **Deaktiviere**: ☐ Delegate IDE build/run actions to Maven
3. **OK** klicken

**Aber:** Für dieses Projekt ist die Application Run Configuration besser!

## Warum passiert das Problem?

IntelliJ setzt bei `exec:exec` das Working Directory auf einen Windows-Pfad (`/wsl.localhost/...`), den das Maven exec-plugin nicht verarbeiten kann. Die Application Run Configuration umgeht dieses Problem komplett.

## Prüfen Sie vorher:

```bash
# JasperReports Service muss laufen!
jasper-status

# Falls nicht:
jasper-start
```

---

**Verwenden Sie die Application Run Configuration `InvoiceExample`** - damit haben Sie keine Probleme! ✅

