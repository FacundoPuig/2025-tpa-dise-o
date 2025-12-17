package ar.utn.ba.dsi.fuenteDinamica;

import ar.utn.ba.dsi.fuenteDinamica.config.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

						.requestMatchers(HttpMethod.GET, "/dinamica/hechos", "/dinamica/hechos/{id}").permitAll()
						.requestMatchers(HttpMethod.POST, "/dinamica/hechos").permitAll()
						.requestMatchers(HttpMethod.PUT, "/dinamica/hechos/*/ocultar").permitAll()
						.requestMatchers("/uploads/**").permitAll()
						.requestMatchers(HttpMethod.PUT, "/dinamica/hechos/{id}/editar").authenticated()
						.requestMatchers("/dinamica/hechos/pendientes").authenticated()
						.requestMatchers("/dinamica/hechos/*/aceptar-con-sugerencia").authenticated()
						.requestMatchers("/dinamica/hechos/*/aprobar").authenticated()
						.requestMatchers("/dinamica/hechos/*/rechazar").authenticated()
						.requestMatchers("/dinamica/hechos/*/etiqueta").authenticated()
						.requestMatchers(HttpMethod.POST, "/dinamica/hechos/{id}/solicitar-eliminacion").authenticated()

						.requestMatchers("/dinamica/ediciones/**").authenticated()
						.requestMatchers(HttpMethod.GET, "/dinamica/solicitudes-eliminacion").authenticated()
						.requestMatchers(HttpMethod.PUT, "/dinamica/solicitudes-eliminacion/**").authenticated()
						.requestMatchers("/actuator/**").permitAll()

						.anyRequest().authenticated()
				)
				.cors(cors -> cors.configurationSource(request -> {
					var corsConfig = new org.springframework.web.cors.CorsConfiguration();

					corsConfig.setAllowedOriginPatterns(List.of("*"));

					corsConfig.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
					corsConfig.setAllowedHeaders(List.of("*"));
					corsConfig.setAllowCredentials(true);
					return corsConfig;
				}))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}