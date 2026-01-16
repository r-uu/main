# 🚀 JasperReports Docker - Schnellstart

## 1. Service starten (einmalig ~2 Minuten beim ersten Mal)

```bash
jasper-start
```

## 2. Test-Report generieren

```bash
jasper-test
```

## 3. Eigenen Report generieren

```bash
# Via REST API
curl -X POST http://localhost:8090/api/report/generate \
  -H "Content-Type: application/json" \
  -d '{
    "template": "test.jrxml",
    "format": "pdf",
    "parameters": {
      "title": "Mein Test Report"
    }
  }'

# Oder via Alias + Skript
jasper generate test.jrxml pdf
```

## 4. Report abholen

```bash
# Reports liegen in:
ls -la config/shared/docker/jasperreports/output/

# Kopieren
cp config/shared/docker/jasperreports/output/*.pdf ~/Downloads/
```

## 5. Von deiner Java-App aufrufen

```java
// Simple HTTP Call
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8090/api/report/generate"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString("""
        {
          "template": "invoice.jrxml",
          "format": "pdf",
          "parameters": {"customerName": "Max Mustermann"}
        }
        """))
    .build();
    
var response = client.send(request, HttpResponse.BodyHandlers.ofString());
// Parse JSON Response und hole outputFile
```

## Aliase

```bash
jasper-start      # Service starten
jasper-stop       # Service stoppen  
jasper-status     # Status prüfen
jasper-logs       # Live-Logs anzeigen
jasper-test       # Test-Report generieren
jasper-clean      # Output-Verzeichnis leeren
jasper            # Zeigt alle Befehle
```

## Service-URL

http://localhost:8090

## Mehr Details

Siehe: [config/shared/docker/jasperreports/README.md](jasperreports/README.md)

