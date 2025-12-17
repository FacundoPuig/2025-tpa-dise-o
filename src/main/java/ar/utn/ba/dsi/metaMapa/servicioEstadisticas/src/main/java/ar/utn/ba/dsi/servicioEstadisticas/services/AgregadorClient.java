package ar.utn.ba.dsi.servicioEstadisticas.services;

import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input.ColeccionDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input.HechoDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input.RegistroCambioEstadoDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input.SolicitudDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
public class AgregadorClient {

	private final WebClient webClient;

	public AgregadorClient(WebClient.Builder builder, @Value("${agregador.ruta}") String rutaAgregador) { //si pongo la ruta por fuera, me podria dar un nullException porque tarda mas q el constructor
		this.webClient = builder.baseUrl(rutaAgregador).build();
	}

	// get de hechos
	public List<HechoDTO> getHechos() {
		return webClient.get()
				.uri("/colecciones/hechosConHandleID") // TODO Verificar q los hechos mandados del Agregador tengan el handlID en el dto Output
				.retrieve()
				.bodyToFlux(HechoDTO.class)
				.collectList()
				.block();
	}

	// get de colecciones
	public List<ColeccionDTO> getColecciones() {
		return webClient.get()
				.uri("/colecciones")
				.retrieve()
				.bodyToFlux(ColeccionDTO.class)
				.collectList()
				.block();
	}

	// get de los registros
	public List<RegistroCambioEstadoDTO> getRegistrosDeSolicitudes() {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/solicitudes/registros")
						.build())
				.retrieve()
				.bodyToFlux(RegistroCambioEstadoDTO.class)
				.collectList()
				.block();
	}

	public List<SolicitudDTO> getSolicitudes() {
		return webClient.get()
				.uri("/solicitudes")
				.retrieve()
				.bodyToFlux(SolicitudDTO.class)
				.collectList()
				.block();
	}

	/*public SolicitudDTO getSolicitudPorId(long id) {
		try {
			return webClient.get()
					.uri("/solicitudes/mostrar-solicitud/{id}", id)
					.retrieve()
					.bodyToMono(SolicitudDTO.class)
					.onErrorResume(e -> {
						System.err.println("Error al obtener solicitud " + id + ": " + e.getMessage());
						return Mono.empty(); // en caso de error devuelve null
					})
					.block(); // bloquea hasta obtener la respuesta (simple y directo)
		} catch (Exception e) {
			System.err.println("Error llamando al agregador: " + e.getMessage());
			return null;
		}
	}*/
}
