package ar.utn.ba.dsi.servicioAgregador.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	public WebClient.Builder webClientBuilder() {
		// Aumentamos el límite de memoria a 50 MB (para que entren los 10k hechos sobrados) -- tiraba error cuando cargaba los hechos de estatica
		// java.io.IOException: Se ha anulado una conexión establecida por el software en su equipo host. ❌ Error en fuente [ESTATICA]: Se ha anulado una conexión establecida por el software en su equipo host.
		final int size = 50 * 1024 * 1024;

		ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
				.build();

		return WebClient.builder()
				.exchangeStrategies(strategies);
	}
}