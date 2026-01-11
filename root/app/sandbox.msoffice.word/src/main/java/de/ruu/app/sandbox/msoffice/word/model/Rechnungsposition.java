package de.ruu.app.sandbox.msoffice.word.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Einzelne Position auf einer Rechnung.
 *
 * <p>Beispiel-Verwendung:
 * <pre>
 * Rechnungsposition position = Rechnungsposition.builder()
 *     .positionsnummer(1)
 *     .beschreibung("Beratungsleistung")
 *     .menge(5.0)
 *     .einheit("Stunden")
 *     .einzelpreis(new BigDecimal("80.00"))
 *     .build();
 *
 * BigDecimal gesamt = position.getGesamtpreis(); // 400.00
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rechnungsposition
{
	/** Laufende Nummer der Position */
	private Integer positionsnummer;

	/** Beschreibung der Leistung/des Produkts */
	private String beschreibung;

	/** Menge */
	private Double menge;

	/** Einheit (z.B. "Stück", "Stunden", "kg") */
	private String einheit;

	/** Einzelpreis (netto) */
	private BigDecimal einzelpreis;

	/** Steuersatz in Prozent (z.B. 19.0 für 19% MwSt.) */
	@Builder.Default
	private Double steuersatz = 19.0;

	/**
	 * Berechnet den Gesamtpreis (Menge × Einzelpreis).
	 *
	 * @return Gesamtpreis ohne Steuer
	 */
	public BigDecimal getGesamtpreis()
	{
		if (menge == null || einzelpreis == null)
		{
			return BigDecimal.ZERO;
		}
		return einzelpreis.multiply(BigDecimal.valueOf(menge))
				.setScale(2, java.math.RoundingMode.HALF_UP);
	}

	/**
	 * Berechnet den Steuerbetrag.
	 *
	 * @return Steuerbetrag
	 */
	public BigDecimal getSteuerbetrag()
	{
		BigDecimal gesamt = getGesamtpreis();
		if (steuersatz == null)
		{
			return BigDecimal.ZERO;
		}
		return gesamt.multiply(BigDecimal.valueOf(steuersatz / 100.0))
				.setScale(2, java.math.RoundingMode.HALF_UP);
	}

	/**
	 * Berechnet den Gesamtpreis inkl. Steuer.
	 *
	 * @return Bruttopreis
	 */
	public BigDecimal getBruttopreis()
	{
		return getGesamtpreis().add(getSteuerbetrag())
				.setScale(2, java.math.RoundingMode.HALF_UP);
	}
}

