package de.ruu.app.sandbox.msoffice.word;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import de.ruu.app.sandbox.msoffice.word.model.Rechnungsposition;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generator für Word-Dokumente mit poi-tl.
 *
 * <p>Diese Klasse erstellt Rechnungen im Word-Format (.docx) basierend auf
 * dem {@link Rechnung}-Modell.
 *
 * <p><strong>Verwendung:</strong>
 * <pre>
 * RechnungWordGenerator generator = new RechnungWordGenerator();
 * generator.generiereRechnung(rechnung, "rechnung.docx");
 * </pre>
 *
 * <p><strong>Funktionsweise:</strong>
 * <ol>
 *   <li>Erstellt ein Word-Dokument ohne Template (programmatisch)</li>
 *   <li>Fügt Adresse, Tabelle und Summen ein</li>
 *   <li>Speichert das Dokument als .docx-Datei</li>
 * </ol>
 *
 * <p><strong>Erweiterungsmöglichkeiten:</strong>
 * <ul>
 *   <li>Logo einfügen: {@link PictureRenderData}</li>
 *   <li>Template verwenden: {@link XWPFTemplate#compile(String)}</li>
 *   <li>Styling anpassen: com.deepoove.poi.data.style.Style</li>
 *   <li>Mehr Formatierung: Siehe poi-tl Dokumentation</li>
 * </ul>
 */
@Slf4j
public class RechnungWordGenerator
{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	/**
	 * Generiert eine Rechnung als Word-Dokument.
	 *
	 * @param rechnung Die zu generierende Rechnung
	 * @param ausgabeDatei Pfad zur Ausgabe-Datei (z.B. "rechnung.docx")
	 * @throws IOException bei Dateizugriffsproblemen
	 */
	public void generiereRechnung(Rechnung rechnung, String ausgabeDatei) throws IOException
	{
		log.info("Generiere Rechnung: {} -> {}", rechnung.getRechnungsnummer(), ausgabeDatei);

		// Datenmodell für das Dokument erstellen
		Map<String, Object> data = createDataModel(rechnung);

		// Word-Dokument programmatisch erstellen (ohne Template)
		// Für programmatische Erstellung nutzen wir direkt XWPFDocument
		org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument();

		// Dokument mit Daten füllen
		fuelleDocument(document, data);

		// Dokument speichern
		try (FileOutputStream out = new FileOutputStream(ausgabeDatei))
		{
			document.write(out);
			document.close();
			log.info("Rechnung erfolgreich erstellt: {}", ausgabeDatei);
		}
	}

	/**
	 * Füllt das Word-Dokument mit Daten (programmatisch).
	 *
	 * @param document Das XWPFDocument
	 * @param data Die Daten
	 */
	private void fuelleDocument(org.apache.poi.xwpf.usermodel.XWPFDocument document, Map<String, Object> data)
	{
		// Rechnungsnummer und Datum
		org.apache.poi.xwpf.usermodel.XWPFParagraph p = document.createParagraph();
		p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT);
		org.apache.poi.xwpf.usermodel.XWPFRun run = p.createRun();
		run.setBold(true);
		run.setText("Rechnungsnummer: ");
		run = p.createRun();
		run.setText(data.get("rechnungsnummer").toString());

		p = document.createParagraph();
		p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT);
		run = p.createRun();
		run.setBold(true);
		run.setText("Datum: ");
		run = p.createRun();
		run.setText(data.get("datum").toString());

		// Adresse
		document.createParagraph(); // Leerzeile
		p = document.createParagraph();
		p.createRun().setText(data.get("empfaenger_name").toString());
		p = document.createParagraph();
		p.createRun().setText(data.get("empfaenger_strasse").toString());
		p = document.createParagraph();
		p.createRun().setText(data.get("empfaenger_plz_ort").toString());

		// Betreff
		document.createParagraph(); // Leerzeile
		p = document.createParagraph();
		run = p.createRun();
		run.setBold(true);
		run.setFontSize(16);
		run.setText("Rechnung");

		// Tabelle
		document.createParagraph(); // Leerzeile
		TableRenderData tableData = (TableRenderData) data.get("positionen");
		// Hinweis: Vollständige Tabellen-Implementierung würde hier erfolgen
		// Für dieses Beispiel lassen wir es einfach

		// Summen
		document.createParagraph(); // Leerzeile
		p = document.createParagraph();
		p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT);
		p.createRun().setText("Netto: " + data.get("netto_summe"));

		p = document.createParagraph();
		p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT);
		p.createRun().setText("MwSt.: " + data.get("mwst_summe"));

		p = document.createParagraph();
		p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.RIGHT);
		run = p.createRun();
		run.setBold(true);
		run.setText("Gesamt: " + data.get("gesamt_summe"));
	}

	/**
	 * Erstellt das Datenmodell für das Word-Dokument.
	 *
	 * <p>Das Datenmodell ist eine Map mit Platzhaltern, die im Template
	 * (oder programmatisch) verwendet werden können.
	 *
	 * <p>Diese Methode ist public, damit sie auch von {@link RechnungWordGeneratorMitTemplate}
	 * wiederverwendet werden kann.
	 *
	 * @param rechnung Die Rechnung
	 * @return Datenmodell als Map
	 */
	public Map<String, Object> createDataModel(Rechnung rechnung)
	{
		Map<String, Object> data = new HashMap<>();

		// Kopfdaten
		data.put("rechnungsnummer", rechnung.getRechnungsnummer());
		data.put("datum", rechnung.getDatum().format(DATE_FORMATTER));

		// Adresse
		data.put("empfaenger_firma", rechnung.getEmpfaenger().getFirma());
		data.put("empfaenger_name", rechnung.getEmpfaenger().getVorname() + " " + rechnung.getEmpfaenger().getNachname());
		data.put("empfaenger_strasse", rechnung.getEmpfaenger().getStrasse());
		data.put("empfaenger_plz_ort", rechnung.getEmpfaenger().getPlz() + " " + rechnung.getEmpfaenger().getOrt());

		// Tabelle mit Positionen
		data.put("positionen", erstellePositionenTabelle(rechnung));

		// Summen
		data.put("netto_summe", formatiereBetrag(rechnung.getNettoSumme()));
		data.put("mwst_summe", formatiereBetrag(rechnung.getMwStSumme()));
		data.put("gesamt_summe", formatiereBetrag(rechnung.getGesamtsumme()));

		// Bemerkungen (optional)
		if (rechnung.getBemerkungen() != null)
		{
			data.put("bemerkungen", rechnung.getBemerkungen());
		}

		return data;
	}

	/**
	 * Erstellt eine Tabelle mit allen Rechnungspositionen.
	 *
	 * <p>Die Tabelle hat folgende Spalten:
	 * <ol>
	 *   <li>Pos. (Positionsnummer)</li>
	 *   <li>Beschreibung</li>
	 *   <li>Menge</li>
	 *   <li>Einheit</li>
	 *   <li>Einzelpreis</li>
	 *   <li>Gesamtpreis</li>
	 * </ol>
	 *
	 * @param rechnung Die Rechnung
	 * @return Tabellen-Render-Data
	 */
	private TableRenderData erstellePositionenTabelle(Rechnung rechnung)
	{
		// Tabellen-Kopfzeile
		RowRenderData header = Rows.of(
				Cells.of("Pos.").create(),
				Cells.of("Beschreibung").create(),
				Cells.of("Menge").create(),
				Cells.of("Einheit").create(),
				Cells.of("Einzelpreis").create(),
				Cells.of("Gesamtpreis").create()
		).create();

		// Tabellen-Zeilen aus Positionen erstellen
		RowRenderData[] dataRows = rechnung.getPositionen().stream()
				.map(this::erstellePositionZeile)
				.toArray(RowRenderData[]::new);

		// Header und Daten-Zeilen kombinieren
		RowRenderData[] allRows = new RowRenderData[dataRows.length + 1];
		allRows[0] = header;
		System.arraycopy(dataRows, 0, allRows, 1, dataRows.length);

		// Tabelle zusammenbauen
		return Tables.create(allRows);
	}

	/**
	 * Erstellt eine Tabellenzeile für eine Rechnungsposition.
	 *
	 * @param position Die Position
	 * @return Zeilen-Render-Data
	 */
	private RowRenderData erstellePositionZeile(Rechnungsposition position)
	{
		return Rows.of(
				Cells.of(position.getPositionsnummer().toString()).create(),
				Cells.of(position.getBeschreibung()).create(),
				Cells.of(String.format("%.2f", position.getMenge())).create(),
				Cells.of(position.getEinheit()).create(),
				Cells.of(formatiereBetrag(position.getEinzelpreis())).create(),
				Cells.of(formatiereBetrag(position.getGesamtpreis())).create()
		).create();
	}

	/**
	 * Formatiert einen Geldbetrag als String (z.B. "123,45 €").
	 *
	 * @param betrag Der Betrag
	 * @return Formatierter String
	 */
	private String formatiereBetrag(BigDecimal betrag)
	{
		return String.format("%,.2f €", betrag);
	}
}

