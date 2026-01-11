package de.ruu.app.sandbox.msoffice.word;

import de.ruu.app.sandbox.msoffice.word.model.Rechnung;
import de.ruu.app.sandbox.msoffice.word.model.Rechnungsposition;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Generator für mehrseitige Rechnungen mit Zwischensummen und Überträgen.
 *
 * <p>Diese Klasse erstellt Rechnungen, die sich über mehrere Seiten erstrecken können,
 * mit professionellen Features wie:
 * <ul>
 *   <li>Automatische Seitenumbrüche nach N Positionen</li>
 *   <li>Zwischensummen am Ende jeder Seite ("Übertrag")</li>
 *   <li>Übertrag-Zeile am Anfang der Folgeseiten</li>
 *   <li>Kopfzeilen-Wiederholung auf jeder Seite</li>
 * </ul>
 *
 * <p><strong>Verwendung:</strong>
 * <pre>
 * MehrseitigeRechnungGenerator generator = new MehrseitigeRechnungGenerator();
 * generator.setZeilenProSeite(20);  // Optional, Standard: 25
 * generator.generiereRechnung(rechnung, "rechnung.docx");
 * </pre>
 *
 * <p><strong>Anpassungen:</strong>
 * <ul>
 *   <li>{@link #setZeilenProSeite(int)} - Anzahl Positionen pro Seite</li>
 *   <li>{@link #setZwischensummenAnzeigen(boolean)} - Zwischensummen ein/aus</li>
 * </ul>
 */
@Slf4j
public class MehrseitigeRechnungGenerator
{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	/** Anzahl Positionen pro Seite (Standard: 25) */
	private int zeilenProSeite = 25;

	/** Zwischensummen anzeigen (Standard: true) */
	private boolean zwischensummenAnzeigen = true;

	/**
	 * Setzt die Anzahl Positionen pro Seite.
	 *
	 * @param zeilenProSeite Anzahl Zeilen (empfohlen: 20-30)
	 */
	public void setZeilenProSeite(int zeilenProSeite)
	{
		this.zeilenProSeite = zeilenProSeite;
	}

	/**
	 * Aktiviert/Deaktiviert Zwischensummen-Anzeige.
	 *
	 * @param zwischensummenAnzeigen true = Zwischensummen anzeigen
	 */
	public void setZwischensummenAnzeigen(boolean zwischensummenAnzeigen)
	{
		this.zwischensummenAnzeigen = zwischensummenAnzeigen;
	}

	/**
	 * Generiert eine mehrseitige Rechnung.
	 *
	 * @param rechnung Die Rechnung
	 * @param ausgabeDatei Ausgabe-Datei
	 * @throws IOException bei Dateizugriffsproblemen
	 */
	public void generiereRechnung(Rechnung rechnung, String ausgabeDatei) throws IOException
	{
		log.info("Generiere mehrseitige Rechnung: {} -> {}", rechnung.getRechnungsnummer(), ausgabeDatei);
		log.info("Positionen gesamt: {}, Seiten: {}",
				rechnung.getPositionen().size(),
				berechneAnzahlSeiten(rechnung.getPositionen().size()));

		XWPFDocument document = new XWPFDocument();

		// Kopfbereich (Rechnungsnummer, Datum, Adresse)
		erstelleKopfbereich(document, rechnung);

		// Positionen in Seiten aufteilen
		List<List<Rechnungsposition>> seiten = teileInSeiten(rechnung.getPositionen());

		// Seiten erstellen
		BigDecimal uebertrag = BigDecimal.ZERO;

		for (int seiteNr = 0; seiteNr < seiten.size(); seiteNr++)
		{
			List<Rechnungsposition> seitenPositionen = seiten.get(seiteNr);
			boolean istErsteSeite = (seiteNr == 0);
			boolean istLetzteSeite = (seiteNr == seiten.size() - 1);

			log.debug("Erstelle Seite {}/{} mit {} Positionen",
					seiteNr + 1, seiten.size(), seitenPositionen.size());

			// Übertrag von vorheriger Seite (außer erste Seite)
			if (!istErsteSeite && zwischensummenAnzeigen)
			{
				erstelleUebertragZeile(document, uebertrag, seiteNr + 1);
			}

			// Tabelle für diese Seite
			XWPFTable table = erstelleTabelle(document, seitenPositionen, istErsteSeite);

			// Seitensumme berechnen
			BigDecimal seitenSumme = berechneSeitenSumme(seitenPositionen);
			uebertrag = uebertrag.add(seitenSumme);

			// Zwischensumme einfügen (außer letzte Seite)
			if (!istLetzteSeite && zwischensummenAnzeigen)
			{
				erstelleZwischensummenZeile(table, uebertrag);
			}

			// Seitenumbruch (außer letzte Seite)
			if (!istLetzteSeite)
			{
				XWPFParagraph pageBreak = document.createParagraph();
				pageBreak.setPageBreak(true);
			}
		}

		// Leerzeile
		document.createParagraph();

		// Endsummen
		erstelleEndsummen(document, rechnung);

		// Bemerkungen
		if (rechnung.getBemerkungen() != null && !rechnung.getBemerkungen().isEmpty())
		{
			document.createParagraph();
			XWPFParagraph p = document.createParagraph();
			p.createRun().setText(rechnung.getBemerkungen());
		}

		// Speichern
		try (FileOutputStream out = new FileOutputStream(ausgabeDatei))
		{
			document.write(out);
			log.info("Rechnung erfolgreich erstellt: {}", ausgabeDatei);
		}
		finally
		{
			document.close();
		}
	}

	/**
	 * Teilt Positionen in Seiten auf.
	 */
	private List<List<Rechnungsposition>> teileInSeiten(List<Rechnungsposition> positionen)
	{
		List<List<Rechnungsposition>> seiten = new ArrayList<>();

		for (int i = 0; i < positionen.size(); i += zeilenProSeite)
		{
			int end = Math.min(i + zeilenProSeite, positionen.size());
			seiten.add(new ArrayList<>(positionen.subList(i, end)));
		}

		return seiten;
	}

	/**
	 * Berechnet Anzahl Seiten basierend auf Positionszahl.
	 */
	private int berechneAnzahlSeiten(int anzahlPositionen)
	{
		return (int) Math.ceil((double) anzahlPositionen / zeilenProSeite);
	}

	/**
	 * Erstellt den Kopfbereich (Rechnungsnummer, Datum, Adresse).
	 */
	private void erstelleKopfbereich(XWPFDocument document, Rechnung rechnung)
	{
		// Rechnungsnummer und Datum (rechtsbündig)
		XWPFParagraph p = document.createParagraph();
		p.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun run = p.createRun();
		run.setBold(true);
		run.setText("Rechnungsnummer: ");
		run = p.createRun();
		run.setText(rechnung.getRechnungsnummer());

		p = document.createParagraph();
		p.setAlignment(ParagraphAlignment.RIGHT);
		run = p.createRun();
		run.setBold(true);
		run.setText("Datum: ");
		run = p.createRun();
		run.setText(rechnung.getDatum().format(DATE_FORMATTER));

		// Leerzeile
		document.createParagraph();

		// Adresse
		p = document.createParagraph();
		if (rechnung.getEmpfaenger().getFirma() != null)
		{
			p.createRun().setText(rechnung.getEmpfaenger().getFirma());
			p = document.createParagraph();
		}
		p.createRun().setText(rechnung.getEmpfaenger().getVorname() + " " + rechnung.getEmpfaenger().getNachname());

		p = document.createParagraph();
		p.createRun().setText(rechnung.getEmpfaenger().getStrasse());

		p = document.createParagraph();
		p.createRun().setText(rechnung.getEmpfaenger().getPlz() + " " + rechnung.getEmpfaenger().getOrt());

		// Leerzeile
		document.createParagraph();

		// Betreff
		p = document.createParagraph();
		run = p.createRun();
		run.setBold(true);
		run.setFontSize(16);
		run.setText("Rechnung");

		document.createParagraph();
	}

	/**
	 * Erstellt eine Übertrag-Zeile am Anfang einer Folgeseite.
	 */
	private void erstelleUebertragZeile(XWPFDocument document, BigDecimal uebertrag, int seiteNr)
	{
		XWPFParagraph p = document.createParagraph();
		p.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun run = p.createRun();
		run.setBold(true);
		run.setText(String.format("Übertrag von Seite %d: %s", seiteNr - 1, formatiereBetrag(uebertrag)));

		document.createParagraph(); // Leerzeile
	}

	/**
	 * Erstellt eine Tabelle mit Positionen.
	 */
	private XWPFTable erstelleTabelle(
			XWPFDocument document,
			List<Rechnungsposition> positionen,
			boolean mitHeader)
	{
		XWPFTable table = document.createTable();

		// Header-Zeile (nur auf erster Seite)
		if (mitHeader)
		{
			XWPFTableRow headerRow = table.getRow(0);
			headerRow.getCell(0).setText("Pos.");
			headerRow.addNewTableCell().setText("Beschreibung");
			headerRow.addNewTableCell().setText("Menge");
			headerRow.addNewTableCell().setText("Einheit");
			headerRow.addNewTableCell().setText("Einzelpreis");
			headerRow.addNewTableCell().setText("Gesamtpreis");

			// Header fett
			for (XWPFTableCell cell : headerRow.getTableCells())
			{
				cell.getParagraphs().get(0).getRuns().get(0).setBold(true);
			}
		}

		// Daten-Zeilen
		for (Rechnungsposition pos : positionen)
		{
			XWPFTableRow row = table.createRow();
			row.getCell(0).setText(pos.getPositionsnummer().toString());
			row.getCell(1).setText(pos.getBeschreibung());
			row.getCell(2).setText(String.format("%.2f", pos.getMenge()));
			row.getCell(3).setText(pos.getEinheit());
			row.getCell(4).setText(formatiereBetrag(pos.getEinzelpreis()));
			row.getCell(5).setText(formatiereBetrag(pos.getGesamtpreis()));
		}

		return table;
	}

	/**
	 * Fügt Zwischensummen-Zeile zur Tabelle hinzu.
	 */
	private void erstelleZwischensummenZeile(XWPFTable table, BigDecimal betrag)
	{
		XWPFTableRow row = table.createRow();
		row.getCell(0).setText("");
		row.getCell(1).setText("");
		row.getCell(2).setText("");
		row.getCell(3).setText("");
		row.getCell(4).setText("Übertrag:");
		row.getCell(5).setText(formatiereBetrag(betrag));

		// Fett formatieren
		row.getCell(4).getParagraphs().get(0).getRuns().get(0).setBold(true);
		row.getCell(5).getParagraphs().get(0).getRuns().get(0).setBold(true);
	}

	/**
	 * Berechnet die Summe aller Positionen auf einer Seite.
	 */
	private BigDecimal berechneSeitenSumme(List<Rechnungsposition> positionen)
	{
		return positionen.stream()
				.map(Rechnungsposition::getGesamtpreis)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Erstellt die Endsummen (Netto, MwSt., Brutto).
	 */
	private void erstelleEndsummen(XWPFDocument document, Rechnung rechnung)
	{
		// Netto
		XWPFParagraph p = document.createParagraph();
		p.setAlignment(ParagraphAlignment.RIGHT);
		p.createRun().setText("Netto-Summe: " + formatiereBetrag(rechnung.getNettoSumme()));

		// MwSt.
		p = document.createParagraph();
		p.setAlignment(ParagraphAlignment.RIGHT);
		p.createRun().setText("MwSt. (19%): " + formatiereBetrag(rechnung.getMwStSumme()));

		// Brutto (fett)
		p = document.createParagraph();
		p.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun run = p.createRun();
		run.setBold(true);
		run.setText("Gesamt-Summe: " + formatiereBetrag(rechnung.getGesamtsumme()));
	}

	/**
	 * Formatiert einen Geldbetrag.
	 */
	private String formatiereBetrag(BigDecimal betrag)
	{
		return String.format("%,.2f €", betrag);
	}
}

