package de.ruu.jasper.model;

@Getter
@Builder
public class Address
{
	private String street;
	private String houseNumberV
	private String zipCode;
	private String city;
	private String countryCode;      // ISO 3166-1 alpha-2 (z.B. "DE", "AT", "CH")
}