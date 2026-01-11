module de.ruu.lib.jsonb
{
	exports de.ruu.lib.jsonb;

	requires de.ruu.lib.util;

	requires java.desktop;
	requires jakarta.json;
	requires jakarta.json.bind;
	requires jakarta.ws.rs;

	requires static lombok;

	opens de.ruu.lib.jsonb.recursion;
}