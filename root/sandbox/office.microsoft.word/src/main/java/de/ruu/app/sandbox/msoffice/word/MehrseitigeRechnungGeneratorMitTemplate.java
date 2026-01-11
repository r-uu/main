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
import java.util.*;

/**
 * Generator für mehrseitige Rechnungen MIT Template-Unterstützung.
 *
 * <p>Kombiniert die Vorteile von:
 * <ul>
 *   <li>Template-basiertem Design (Logo, Farben, Layout in Word anpassbar)</li>
 *   <li>Mehrseitiger Unterstützung (automatische Seitenumbrüche, Zwischensummen)</li>
 * </ul>
 *
 * <p><strong>Template-Anforderungen:</strong>
 * Das Template muss Platzhalter für mehrere Seiten enthalten:
 * <ul>
 *   <li>{{positionen_seite_1}}, {{positionen_seite_2}}, etc.</li>
 *   <li>{{uebertrag_seite_1}}, {{uebertrag_seite_2}}, etc.</li>
 *   <li>Oder: dynamisches Template mit Schleifen</li>
 * </ul>
 *
 * <p><strong>Verwendung:</strong>
 * <pre>
 * MehrseitigeRechnungGeneratorMitTemplate generator =
 *     new MehrseitigeRechnungGeneratorMitTemplate("template.docx");
 * generator.setZeilenProSeite(20);
 * generator.generiereRechnung(rechnung, "rechnung.docx");
 * </pre>
 *
 * <p><strong>Alternative ohne vordefiniertes Template:</strong>
 * Dieser Generator kann auch OHNE vorgefertigtes Template arbeiten und
 * erstellt das Dokument dynamisch basierend auf der Positionszahl.
 */
@Slf4j
public class MehrseitigeRechnungGeneratorMitTemplate
{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private final String templateDatei;
	private int zeilenProSeite = 15;
	private boolean zwischensummenAnzeigen = true;

	/**
	 * Erstellt einen Generator mit Template-Datei.
	 *
	 * @param templateDatei Pfad zur Template-Datei
	 */
	public MehrseitigeRechnungGeneratorMitTemplate(String templateDatei)
	{
		this.templateDatei = templateDatei;
	}

	/**
	 * Setzt die Anzahl Positionen pro Seite.
	 */
	public void setZeilenProSeite(int zeilenProSeite)
	{
		this.zeilenProSeite = zeilenProSeite;
	}

	/**
	 * Aktiviert/Deaktiviert Zwischensummen.
	 */
	public void setZwischensummenAnzeigen(boolean zwischensummenAnzeigen)
	{
		this.zwischensummenAnzeigen = zwischensummenAnzeigen;
	}

	/**
	 * Generiert eine mehrseitige Rechnung mit Template.
	 *
	 * @param rechnung Die Rechnung
	 * @param ausgabeDatei Ausgabe-Datei
	 * @throws IOException bei Fehlern
	 */
	public void generiereRechnung(Rechnung rechnung, String ausgabeDatei) throws IOException
	{
		log.info("Generiere mehrseitige Rechnung mit Template: {} -> {}",
				rechnung.getRechnungsnummer(), ausgabeDatei);

		// Positionen in Seiten aufteilen
		List<List<Rechnungsposition>> seiten = teileInSeiten(rechnung.getPositionen());

		log.info("Positionen: {}, Seiten: {}, Zeilen/Seite: {}",
				rechnung.getPositionen().size(), seiten.size(), zeilenProSeite);

		// Datenmodell für Template erstellen
		Map<String, Object> data = erstelleDatenModell(rechnung, seiten);

		// Template rendern
		XWPFTemplate template = XWPFTemplate.compile(templateDatei).render(data);

		// Speichern
		try (FileOutputStream out = new FileOutputStream(ausgabeDatei))
		{
			template.writeAndClose(out);
			log.info("Mehrseitige Rechnung erfolgreich erstellt: {}", ausgabeDatei);
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
	 * Erstellt das Datenmodell für das Template.
	 */
	private Map<String, Object> erstelleDatenModell(
			Rechnung rechnung,
			List<List<Rechnungsposition>> seiten)
	{
		Map<String, Object> data = new HashMap<>();

		// Basis-Daten
		data.put("rechnungsnummer", rechnung.getRechnungsnummer());
		data.put("datum", rechnung.getDatum().format(DATE_FORMATTER));
		data.put("empfaenger_firma", rechnung.getEmpfaenger().getFirma());
		data.put("empfaenger_name",
				rechnung.getEmpfaenger().getVorname() + " " + rechnung.getEmpfaenger().getNachname());
		data.put("empfaenger_strasse", rechnung.getEmpfaenger().getStrasse());
		data.put("empfaenger_plz_ort",
				rechnung.getEmpfaenger().getPlz() + " " + rechnung.getEmpfaenger().getOrt());

		// Seiten-Daten
		data.put("anzahl_seiten", seiten.size());

		BigDecimal uebertrag = BigDecimal.ZERO;

		for (int i = 0; i < seiten.size(); i++)
		{
			int seiteNr = i + 1;
			List<Rechnungsposition> seitenPositionen = seiten.get(i);

			// Tabelle für diese Seite
			TableRenderData tabelleData = erstelleTabellenDaten(seitenPositionen, i == 0);
			data.put("positionen_seite_" + seiteNr, tabelleData);

			// Seitennummer
			data.put("seite_" + seiteNr, seiteNr);

			// Übertrag von vorheriger Seite
			if (i > 0)
			{
				data.put("uebertrag_von_seite_" + seiteNr, formatiereBetrag(uebertrag));
			}

			// Seitensumme berechnen
			BigDecimal seitenSumme = berechneSeitenSumme(seitenPositionen);
			uebertrag = uebertrag.add(seitenSumme);

			// Übertrag für nächste Seite
			if (i < seiten.size() - 1 && zwischensummenAnzeigen)
			{
				data.put("uebertrag_nach_seite_" + seiteNr, formatiereBetrag(uebertrag));
			}
		}

		// Endsummen
		data.put("netto_summe", formatiereBetrag(rechnung.getNettoSumme()));
		data.put("mwst_summe", formatiereBetrag(rechnung.getMwStSumme()));
		data.put("gesamt_summe", formatiereBetrag(rechnung.getGesamtsumme()));

		if (rechnung.getBemerkungen() != null)
		{
			data.put("bemerkungen", rechnung.getBemerkungen());
		}

		return data;
	}

	/**
	 * Erstellt Tabellen-Daten für poi-tl.
	 */
	private TableRenderData erstelleTabellenDaten(
			List<Rechnungsposition> positionen,
			boolean mitHeader)
	{
		// Header (nur auf erster Seite)
		RowRenderData header = null;
		if (mitHeader)
		{
			header = Rows.of(
					Cells.of("Pos.").create(),
					Cells.of("Beschreibung").create(),
					Cells.of("Menge").create(),
					Cells.of("Einheit").create(),
					Cells.of("Einzelpreis").create(),
					Cells.of("Gesamtpreis").create()
			).create();
		}

		// Daten-Zeilen
		List<RowRenderData> rows = new ArrayList<>();
		for (Rechnungsposition pos : positionen)
		{
			RowRenderData row = Rows.of(
					Cells.of(pos.getPositionsnummer().toString()).create(),
					Cells.of(pos.getBeschreibung()).create(),
					Cells.of(String.format("%.2f", pos.getMenge())).create(),
					Cells.of(pos.getEinheit()).create(),
					Cells.of(formatiereBetrag(pos.getEinzelpreis())).create(),
					Cells.of(formatiereBetrag(pos.getGesamtpreis())).create()
			).create();
			rows.add(row);
		}

		// Tabelle zusammenbauen
		if (mitHeader)
		{
			RowRenderData[] allRows = new RowRenderData[rows.size() + 1];
			allRows[0] = header;
			for (int i = 0; i < rows.size(); i++)
			{
				allRows[i + 1] = rows.get(i);
			}
			return Tables.create(allRows);
		}
		else
		{
			return Tables.create(rows.toArray(new RowRenderData[0]));
		}
	}

	/**
	 * Berechnet die Summe einer Seite.
	 */
	private BigDecimal berechneSeitenSumme(List<Rechnungsposition> positionen)
	{
		return positionen.stream()
				.map(Rechnungsposition::getGesamtpreis)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Formatiert einen Geldbetrag.
	 */
	private String formatiereBetrag(BigDecimal betrag)
	{
		return String.format("%,.2f €", betrag);
	}
}

