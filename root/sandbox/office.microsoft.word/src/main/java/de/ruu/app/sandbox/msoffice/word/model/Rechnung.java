package de.ruu.app.sandbox.msoffice.word.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Komplette Rechnung mit Adresse und Positionen.
 *
 * <p>Beispiel-Verwendung:
 * <pre>
 * Rechnung rechnung = Rechnung.builder()
 *     .rechnungsnummer("R-2026-001")
 *     .datum(LocalDate.now())
 *     .empfaenger(adresse)
 *     .positionen(Arrays.asList(pos1, pos2))
 *     .build();
 *
 * BigDecimal summe = rechnung.getGesamtsumme();
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rechnung
{
	/** Rechnungsnummer (z.B. "R-2026-001") */
	private String rechnungsnummer;

	/** Rechnungsdatum */
	private LocalDate datum;

	/** Empfänger-Adresse */
	private Adresse empfaenger;

	/** Liste der Rechnungspositionen */
	@Builder.Default
	private List<Rechnungsposition> positionen = new ArrayList<>();

	/** Optionale Notizen/Bemerkungen */
	private String bemerkungen;

	/**
	 * Berechnet die Netto-Summe aller Positionen.
	 *
	 * @return Gesamtsumme netto
	 */
	public BigDecimal getNettoSumme()
	{
		return positionen.stream()
				.map(Rechnungsposition::getGesamtpreis)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Berechnet die Summe aller Steuerbeträge.
	 *
	 * @return Gesamte MwSt.
	 */
	public BigDecimal getMwStSumme()
	{
		return positionen.stream()
				.map(Rechnungsposition::getSteuerbetrag)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * Berechnet die Brutto-Gesamtsumme (netto + MwSt.).
	 *
	 * @return Gesamtsumme brutto
	 */
	public BigDecimal getGesamtsumme()
	{
		return getNettoSumme().add(getMwStSumme());
	}

	/**
	 * Fügt eine neue Position zur Rechnung hinzu.
	 *
	 * @param position Neue Position
	 */
	public void addPosition(Rechnungsposition position)
	{
		if (positionen == null)
		{
			positionen = new ArrayList<>();
		}
		positionen.add(position);
	}
}

