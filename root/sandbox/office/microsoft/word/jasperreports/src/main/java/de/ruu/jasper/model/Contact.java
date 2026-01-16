package de.ruu.jasper.model;

public class Contact
{
	private String  salutation;      // z.B. "Herr", "Frau"
	private String  firstName;
	private String  lastName;
	private Address address;
	private String  email;           // Validierung via Jakarta Bean Validation @Email
	private String  phone;           // Format: +49 123 456789 (E.164 Standard)

	public Contact(String salutation, String firstName, String lastName, Address address, String email, String phone)
	{
		this.salutation = salutation;
		this.firstName  = firstName;
		this.lastName   = lastName;
		this.address    = address;
		this.email      = email;
		this.phone      = phone;
	}

	public String  getSalutation() { return salutation; }
	public String  getFirstName () { return firstName;  }
	public String  getLastName  () { return lastName;   }
	public Address getAddress   () { return address;    }
	public String  getEmail     () { return email;      }
	public String  getPhone     () { return phone;      }
}
