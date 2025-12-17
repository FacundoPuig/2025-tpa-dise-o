package ar.utn.ba.dsi.servicioUsuarios.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret; // Clave de application.properties

	@Value("${jwt.expiration}")
	private Long expiration; // Expiración de Access Token

	private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 días

	/**
	 * Genera la clave de firma a partir del 'secret' de properties.
	 */
	private SecretKey getSigningKey() {
		byte[] keyBytes = secret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Genera un Access Token.
	 */
	public String generateAccessToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();

		return Jwts.builder()
				.claims(claims)
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * Genera un Refresh Token.
	 */
	public String generateRefreshToken(UserDetails userDetails) {
		return Jwts.builder()
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.issuer("gestion-alumnos-server") // Opcional
				.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
				.claim("type", "refresh")
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * Extrae TODOS los "claims" (datos) del token.
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/**
	 * Helper genérico para extraer un solo claim.
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Extrae el username (el "subject") del token.
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extrae la fecha de expiración del token.
	 */
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Verifica si el token ha expirado.
	 */
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Método público principal para validar un token.
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}