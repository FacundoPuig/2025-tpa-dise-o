package ar.utn.ba.dsi.servicioAgregador.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;

@Component // 1. Lo hacemos Componente para que Spring pueda inyectar valores
public class JwtUtil {

  @Value("${jwt.secret}") // 2. Leemos la clave del application.properties
  private String secret;

  @Getter
	private static Key key; // 3. Usamos una variable estática para la clave

  @PostConstruct
  public void init() {
    // 4. Inicializamos la clave UNA SOLA VEZ con el secreto compartido
    key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

	public static String validarToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key) // 5. Validamos usando la clave correcta
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }
}