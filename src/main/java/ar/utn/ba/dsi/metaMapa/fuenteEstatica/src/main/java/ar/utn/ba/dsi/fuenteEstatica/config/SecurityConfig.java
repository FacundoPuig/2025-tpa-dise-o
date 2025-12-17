package ar.utn.ba.dsi.fuenteEstatica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// Permitir lectura pública (Agregador consume esto)
						.requestMatchers(HttpMethod.GET, "/estatica/**").permitAll()

						// Proteger la carga de CSV (Frontend llama a esto)
						.requestMatchers(HttpMethod.POST, "/estatica/cargar-csv").authenticated()
						.requestMatchers(HttpMethod.PUT, "/estatica/hechos/*/ocultar").permitAll() //ver de usar auth, pero por ahora cualquiera puede ocultar
						.requestMatchers("/actuator/**").permitAll()
						// Cualquier otra cosa requiere auth
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}