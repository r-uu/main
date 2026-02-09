# IntelliJ IDEA Cache-Problem Behebung

## Problem
IntelliJ zeigt Fehler an wie:
```
java: Inkompatible Typen: java.lang.Class<de.ruu.lib.jsonb.recursion.ChildrenAdapter> 
kann nicht in java.lang.Class<? extends jakarta.json.bind.adapter.JsonbAdapter> konvertiert werden
```

**Aber**: Der Code kompiliert erfolgreich mit Maven!

## Ursache
Dies ist ein bekanntes IntelliJ IDEA-Problem mit:
- JPMS (Java Platform Module System)
- Test-module-info.java Dateien
- Cached Modulinformationen

## Lösung

### Option 1: IntelliJ Cache löschen (Empfohlen)
1. **File → Invalidate Caches...**
2. Wählen Sie:
   - ✅ Clear file system cache and Local History
   - ✅ Clear VCS Log caches and indexes
   - ✅ Clear downloaded shared indexes
   - ✅ Invalidate and Restart
3. Klicken Sie auf **"Invalidate and Restart"**

### Option 2: Maven Reimport
1. Öffnen Sie das Maven Tool Window
2. Rechtsklick auf das Projekt
3. Wählen Sie **"Reload All Maven Projects"**

### Option 3: Module neu laden
1. **File → Project Structure → Modules**
2. Löschen Sie alle Module
3. Klicken Sie auf **"+"** → **"Import Module"**
4. Wählen Sie die root pom.xml

### Option 4: Projekt neu öffnen
1. **File → Close Project**
2. Projekt aus der Liste löschen
3. **File → Open** → Wählen Sie das Projektverzeichnis

## Verifikation
Nach der Cache-Löschung sollten die Fehler verschwinden. Sie können verifizieren, dass der Code korrekt ist mit:

```bash
cd /home/r-uu/develop/github/main/root/lib/jsonb
mvn clean test-compile
```

Dieser Befehl sollte **BUILD SUCCESS** zeigen.

## Warum funktioniert Maven?
Maven ignoriert das Test-module-info.java gemäß der Konfiguration in der pom.xml:

```xml
<testExcludes>
    <testExclude>**/module-info.java</testExclude>
</testExcludes>
```

IntelliJ nutzt jedoch das Test-module-info.java für JPMS-konforme Test-Ausführung, was manchmal zu Cache-Inkonsistenzen führt.

## Der Code ist korrekt!
Der `ChildrenAdapter` ist korrekt implementiert:
- Erweitert `AbstractSetAdapter<Child>`
- `AbstractSetAdapter` implementiert `JsonbAdapter<Set<T>, JsonValue>`
- Daher implementiert `ChildrenAdapter` indirekt `JsonbAdapter<Set<Child>, JsonValue>`
- Dies ist kompatibel mit `@JsonbTypeAdapter(ChildrenAdapter.class)` auf einem `Set<Child>` Feld

**→ Dies ist ein IntelliJ-Cache-Problem, kein Code-Problem!**

