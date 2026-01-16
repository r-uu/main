package de.ruu.jasper.model;

public class Address
{
	private String street;
	private String houseNumber;
	private String zipCode;
	private String city;
	private String countryCode;      // ISO 3166-1 alpha-2 (z.B. "DE", "AT", "CH")

	public Address(String street, String houseNumber, String zipCode, String city, String countryCode)
	{
		this.street      = street;
		this.houseNumber = houseNumber;
		this.zipCode     = zipCode;
		this.city        = city;
		this.countryCode = countryCode;
	}

	public String getStreet     () { return street;      }
	public String getHouseNumber()
	{
		return houseNumber;
	}
	public String getZipCode    () { return zipCode;     }
	public String getCity       () { return city;        }
	public String getCountryCode()
	{
		return countryCode;
	}
}