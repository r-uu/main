package de.ruu.app.sandbox.msoffice.word.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Adresse für Rechnungen und Briefe.
 *
 * <p>Beispiel-Verwendung:
 * <pre>
 * Adresse adresse = Adresse.builder()
 *     .firma("Musterfirma GmbH")
 *     .vorname("Max")
 *     .nachname("Mustermann")
 *     .strasse("Musterstraße 123")
 *     .plz("12345")
 *     .ort("Musterstadt")
 *     .build();
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Adresse
{
	/** Firmenname (optional) */
	private String firma;

	/** Vorname */
	private String vorname;

	/** Nachname */
	private String nachname;

	/** Straße mit Hausnummer */
	private String strasse;

	/** Postleitzahl */
	private String plz;

	/** Ort */
	private String ort;

	/** Land (optional) */
	private String land;

	/**
	 * Gibt die vollständige Adresse als mehrzeiligen String zurück.
	 *
	 * @return Formatierte Adresse
	 */
	public String getVollstaendigeAdresse()
	{
		StringBuilder sb = new StringBuilder();

		if (firma != null && !firma.isEmpty())
		{
			sb.append(firma).append("\n");
		}

		sb.append(vorname).append(" ").append(nachname).append("\n");
		sb.append(strasse).append("\n");
		sb.append(plz).append(" ").append(ort);

		if (land != null && !land.isEmpty())
		{
			sb.append("\n").append(land);
		}

		return sb.toString();
	}
}

