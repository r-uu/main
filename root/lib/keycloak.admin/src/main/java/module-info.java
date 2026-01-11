module de.ruu.lib.keycloak.admin
{
	// Keycloak Admin Client (automatic modules)
	requires keycloak.admin.client;
	requires keycloak.client.common.synced;
	
	// JAX-RS API
	requires jakarta.ws.rs;
	
	// Lombok needs java.beans for @Builder
	requires static lombok;
	requires java.desktop; // For java.beans package (used by Lombok)
	
	requires org.slf4j;

	exports de.ruu.lib.keycloak.admin;
	exports de.ruu.lib.keycloak.admin.validation;
}
