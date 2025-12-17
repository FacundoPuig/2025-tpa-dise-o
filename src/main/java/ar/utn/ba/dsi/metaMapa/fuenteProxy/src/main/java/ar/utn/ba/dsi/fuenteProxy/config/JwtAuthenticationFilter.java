package ar.utn.ba.dsi.fuenteProxy.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Value("${jwt.secret}")
	private String secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		try {
			Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();

			String username = claims.getSubject();

			// Extraemos roles si vienen en el token, sino asignamos uno genérico
			List<String> roles = claims.get("roles", List.class);
			var authorities = (roles != null) ?
					roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()) :
					List.of(new SimpleGrantedAuthority("ROLE_USER"));

			if (username != null) {
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (Exception e) {
			logger.error("Error validando JWT en Estática: " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}