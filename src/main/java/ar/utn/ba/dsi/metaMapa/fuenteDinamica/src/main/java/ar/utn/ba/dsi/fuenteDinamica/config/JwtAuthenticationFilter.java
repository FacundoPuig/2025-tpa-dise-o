package ar.utn.ba.dsi.fuenteDinamica.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Value("${jwt.secret}")
	private String secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 1. Buscar el header Authorization
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 2. Extraer el token (sacando el "Bearer ")
		String token = authHeader.substring(7);

		try {
			// 3. Validar la firma y extraer el usuario (Subject)
			Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();

			String username = claims.getSubject(); // Este es el ID/Email del usuario

			// 4. Guardar la autenticación en el contexto de Spring
			if (username != null) {
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (Exception e) {
			// Si el token está mal o vencido, no hacemos nada (seguirá como anónimo)
			logger.error("Error validando JWT: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}