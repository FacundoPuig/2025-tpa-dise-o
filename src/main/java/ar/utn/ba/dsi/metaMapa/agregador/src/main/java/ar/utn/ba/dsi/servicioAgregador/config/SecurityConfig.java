package ar.utn.ba.dsi.servicioAgregador.config;

import ar.utn.ba.dsi.servicioAgregador.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(Customizer.withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> {

					auth.requestMatchers("/graphql/**", "/graphiql/**").permitAll();
					auth.requestMatchers("/favicon.ico", "/assets/**", "/error").permitAll();

					auth.requestMatchers("/api/auth/**").permitAll(); // Auth público

					auth.requestMatchers("/hechos/categorias").permitAll(); // Categorías públicas
					auth.requestMatchers(HttpMethod.GET, "/hechos/{id}").permitAll(); // para un anonimo pueda tener info del hecho

					auth.requestMatchers(HttpMethod.GET, "/colecciones/**").permitAll();
					auth.requestMatchers(HttpMethod.GET, "/colecciones/*/hechos/navegacion").permitAll();
					auth.requestMatchers(HttpMethod.GET, "/colecciones/*/hechos/filtrar").permitAll();

					auth.requestMatchers("/solicitudes").permitAll();
					auth.requestMatchers("/solicitudes/**").permitAll();

					auth.requestMatchers("/error").permitAll();

					auth.requestMatchers("agregacion/actualizar").permitAll();//TODO ELIMINHAR ESTO DESPUES DE PRUEBA
					auth.requestMatchers("agregacion/actualiza-consenso").permitAll();//TODO ELIMINHAR ESTO DESPUES DE PRUEBA
					auth.requestMatchers("estadisticas/calcular-todo").permitAll();//TODO ELIMINHAR ESTO DESPUES DE PRUEBA
					auth.requestMatchers("/actuator/**").permitAll();
					
					auth.anyRequest().authenticated(); // El resto requiere token

				})
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*")); // Permitir todo origen
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


}