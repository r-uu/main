package de.ruu.lib.keycloak.admin.validation;

import de.ruu.lib.keycloak.admin.validation.JwtTokenParser.TokenInfo;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtTokenParser}.
 * 
 * @author r-uu
 * @since 2025-12-27
 */
class JwtTokenParserTest
{
	/**
	 * Sample JWT token for testing (manually created).
	 * Payload: {
	 *   "iss": "http://localhost:8080/realms/test-realm",
	 *   "sub": "user-123",
	 *   "aud": ["jeeeraaah-backend", "account"],
	 *   "exp": 1735680000,
	 *   "iat": 1735679700,
	 *   "preferred_username": "testuser",
	 *   "realm_access": {
	 *     "roles": ["task-read", "task-write"]
	 *   }
	 * }
	 */
	private static final String SAMPLE_TOKEN = createSampleToken();
	
	private static String createSampleToken()
	{
		// Header: {"alg":"RS256","typ":"JWT"}
		String header = base64UrlEncode("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
		
		// Payload with test data
		String payload = base64UrlEncode(
				"{" +
				"\"iss\":\"http://localhost:8080/realms/test-realm\"," +
				"\"sub\":\"user-123\"," +
				"\"aud\":[\"jeeeraaah-backend\",\"account\"]," +
				"\"exp\":1735680000," +
				"\"iat\":1735679700," +
				"\"preferred_username\":\"testuser\"," +
				"\"realm_access\":{\"roles\":[\"task-read\",\"task-write\"]}" +
				"}");
		
		// Signature (not validated in this simple parser)
		String signature = "fake-signature";
		
		return header + "." + payload + "." + signature;
	}
	
	private static String base64UrlEncode(String input)
	{
		return Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(input.getBytes());
	}
	
	@Test
	void testParseToken_ValidToken()
	{
		TokenInfo tokenInfo = JwtTokenParser.parseToken(SAMPLE_TOKEN);
		
		assertNotNull(tokenInfo);
		assertEquals("http://localhost:8080/realms/test-realm", tokenInfo.getIssuer());
		assertEquals("user-123", tokenInfo.getSubject());
		assertEquals("testuser", tokenInfo.getPreferredUsername());
		assertEquals(1735680000L, tokenInfo.getExpirationTime());
		assertEquals(1735679700L, tokenInfo.getIssuedAt());
		
		// Check audiences
		assertTrue(tokenInfo.getAudiences().contains("jeeeraaah-backend"));
		assertTrue(tokenInfo.getAudiences().contains("account"));
		assertEquals(2, tokenInfo.getAudiences().size());
		
		// Check roles
		assertTrue(tokenInfo.getRoles().contains("task-read"));
		assertTrue(tokenInfo.getRoles().contains("task-write"));
		assertEquals(2, tokenInfo.getRoles().size());
	}
	
	@Test
	void testParseToken_GetLifetime()
	{
		TokenInfo tokenInfo = JwtTokenParser.parseToken(SAMPLE_TOKEN);
		
		// Lifetime should be exp - iat = 1735680000 - 1735679700 = 300 seconds
		assertEquals(300L, tokenInfo.getLifetimeSeconds());
	}
	
	@Test
	void testParseToken_NullToken()
	{
		assertThrows(IllegalArgumentException.class, () -> {
			JwtTokenParser.parseToken(null);
		});
	}
	
	@Test
	void testParseToken_BlankToken()
	{
		assertThrows(IllegalArgumentException.class, () -> {
			JwtTokenParser.parseToken("   ");
		});
	}
	
	@Test
	void testParseToken_InvalidFormat_TwoParts()
	{
		assertThrows(IllegalArgumentException.class, () -> {
			JwtTokenParser.parseToken("header.payload");
		});
	}
	
	@Test
	void testParseToken_InvalidFormat_FourParts()
	{
		assertThrows(IllegalArgumentException.class, () -> {
			JwtTokenParser.parseToken("header.payload.signature.extra");
		});
	}
	
	@Test
	void testTokenInfo_IsExpired()
	{
		// Create a token that expired in the past
		String expiredPayload = base64UrlEncode(
				"{" +
				"\"iss\":\"http://localhost:8080/realms/test-realm\"," +
				"\"exp\":1000000000," +  // Year 2001 - definitely expired
				"\"iat\":999999700," +
				"\"realm_access\":{\"roles\":[]}" +
				"}");
		
		String expiredToken = "header." + expiredPayload + ".signature";
		TokenInfo tokenInfo = JwtTokenParser.parseToken(expiredToken);
		
		assertTrue(tokenInfo.isExpired());
		assertEquals(0L, tokenInfo.getRemainingLifetimeSeconds()); // Should be 0, not negative
	}
	
	@Test
	void testTokenInfo_NotExpired()
	{
		// Create a token that expires in the future
		long futureExp = (System.currentTimeMillis() / 1000) + 3600; // +1 hour
		long futureIat = futureExp - 300; // 5 minutes ago
		
		String futurePayload = base64UrlEncode(
				"{" +
				"\"iss\":\"http://localhost:8080/realms/test-realm\"," +
				"\"exp\":" + futureExp + "," +
				"\"iat\":" + futureIat + "," +
				"\"realm_access\":{\"roles\":[]}" +
				"}");
		
		String futureToken = "header." + futurePayload + ".signature";
		TokenInfo tokenInfo = JwtTokenParser.parseToken(futureToken);
		
		assertFalse(tokenInfo.isExpired());
		assertTrue(tokenInfo.getRemainingLifetimeSeconds() > 3500); // Should be close to 1 hour
		assertTrue(tokenInfo.getRemainingLifetimeSeconds() < 3700);
	}
	
	@Test
	void testParseToken_SingleAudience()
	{
		// Test with single audience string instead of array
		String singleAudPayload = base64UrlEncode(
				"{" +
				"\"iss\":\"http://localhost:8080/realms/test-realm\"," +
				"\"aud\":\"jeeeraaah-backend\"," +  // Single string, not array
				"\"exp\":1735680000," +
				"\"iat\":1735679700," +
				"\"realm_access\":{\"roles\":[]}" +
				"}");
		
		String token = "header." + singleAudPayload + ".signature";
		TokenInfo tokenInfo = JwtTokenParser.parseToken(token);
		
		assertEquals(1, tokenInfo.getAudiences().size());
		assertTrue(tokenInfo.getAudiences().contains("jeeeraaah-backend"));
	}
	
	@Test
	void testParseToken_NoRoles()
	{
		// Test token without realm_access claim
		String noRolesPayload = base64UrlEncode(
				"{" +
				"\"iss\":\"http://localhost:8080/realms/test-realm\"," +
				"\"sub\":\"user-123\"," +
				"\"exp\":1735680000," +
				"\"iat\":1735679700" +
				"}");
		
		String token = "header." + noRolesPayload + ".signature";
		TokenInfo tokenInfo = JwtTokenParser.parseToken(token);
		
		assertNotNull(tokenInfo.getRoles());
		assertTrue(tokenInfo.getRoles().isEmpty());
	}
}
