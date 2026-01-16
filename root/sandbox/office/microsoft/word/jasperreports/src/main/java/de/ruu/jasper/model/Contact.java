public class Contact(
		String salutation;      // z.B. "Herr", "Frau"
		String firstName;
		String lastName;
		Address address;
		String email;           // Validierung via Jakarta Bean Validation @Email
		String phone;           // Format: +49 123 456789 (E.164 Standard)
) {}