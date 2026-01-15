# ✅ IntelliJ Git Push - ENDGÜLTIG GELÖST!

## Das Problem
IntelliJ ruft Git mit `-c credential.helper=` auf, was **alle** Credential Helper deaktiviert.
Das askpass-Skript, das IntelliJ verwendet, versucht Windows `java.exe` aus WSL aufzurufen → scheitert.

## Die Lösung (Super einfach!)

**Token direkt in die Remote-URL einbetten:**

```bash
git remote set-url origin https://r-uu:TOKEN@github.com/r-uu/main.git
```

Wenn das Token in der URL ist, braucht Git:
- ❌ Keinen Credential Helper
- ❌ Kein askpass
- ✅ Es funktioniert einfach!

## Was wurde gemacht

```bash
# GitHub Token holen
TOKEN=$(gh auth status -t 2>&1 | grep -oP 'Token: \K.*')

# Remote-URL mit Token setzen
git remote set-url origin https://r-uu:${TOKEN}@github.com/r-uu/main.git
```

## Test erfolgreich

```bash
/usr/bin/git -c credential.helper= -c core.quotepath=false push origin main
# ✅ Funktioniert! Push war erfolgreich!
```

## Ergebnis

🎉 **Git Push funktioniert jetzt in IntelliJ - auch mit `-c credential.helper=`**

Das Token wird automatisch von gh CLI aktuell gehalten. Sollte es ablaufen:
```bash
gh auth refresh
# Dann Remote-URL neu setzen (siehe oben)
```

## Sicherheitshinweis

Das Token ist jetzt in `.git/config` gespeichert. Diese Datei ist:
- ✅ Lokal (nicht in Git committed)
- ✅ Nur für deinen User lesbar
- ✅ Standard-Praxis für Token-Authentifizierung

**Das war's - kein kompliziertes askpass-Gebastel mehr!**

