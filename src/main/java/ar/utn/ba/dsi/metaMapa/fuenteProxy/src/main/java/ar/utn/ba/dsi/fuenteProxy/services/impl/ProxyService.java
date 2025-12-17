package ar.utn.ba.dsi.fuenteProxy.services.impl;

import ar.utn.ba.dsi.fuenteProxy.models.dtos.external.HechosApiResponseDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.input.HechoProxyInputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.input.SolicitudEliminacionProxyInputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.ColeccionProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.HechoProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.SolicitudEliminacionProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.services.IProxyService;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.external.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ProxyService implements IProxyService {
  private WebClient webClient;
  private String accessToken;
  private final WebClient.Builder webClientBuilder;

  @Value("${api.ddsi.email}")
  private String email;

  @Value("${api.ddsi.password}")
  private String password;

  public ProxyService(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
    this.webClient = webClientBuilder.baseUrl("https://api-ddsi.disilab.ar/public").build();
  }

  private Mono<String> getOrRefreshToken() {
    if (this.accessToken != null) {
      return Mono.just(this.accessToken);
    }
    return authenticateAndGetToken()
        .map(auth -> this.accessToken);
  }


  public Mono<AuthResponseDTO> authenticateAndGetToken() {
    Map<String, String> credentials = Map.of("email", "ddsi@gmail.com", "password", "ddsi2025*");

    return webClient.post()
        .uri("/api/login")
        .bodyValue(credentials)
        .retrieve()
        .bodyToMono(AuthResponseDTO.class)
        .map(response -> {
          AuthResponseDTO auth = response;
          this.accessToken = response.getData().getAccess_token();
          return auth;}
        );
  }

  /* API CATEDRA */

  @Override
  public Mono<List<HechoProxyOutputDTO>> getDesastres() {
    return getOrRefreshToken()
        .flatMap(token -> webClient
            .get()
            .uri("/api/desastres")
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(HechosApiResponseDTO.class)
            .map(response -> {
              return response.getData().stream()
                  .map(this::convertirAOutputDTO)
                  .toList();
            })
        );
  }

  // Método auxiliar de conversión
  private HechoProxyOutputDTO convertirAOutputDTO(HechoProxyInputDTO input) {
    HechoProxyOutputDTO output = new HechoProxyOutputDTO();

    output.setTitulo(input.getTitulo());
    output.setDescripcion(input.getDescripcion());
    output.setCategoria(input.getCategoria());
    output.setLatitud(input.getLatitud());
    output.setLongitud(input.getLongitud());

    output.setFechaAcontecimiento(input.getFecha_hecho());

    output.setNombreOrigen("API Cátedra");
    output.setProvieneDeOrigen("PROXY");
    output.setFechaCarga(java.time.LocalDate.now());

    return output;
  }

  public Mono<HechoProxyInputDTO> getDesastresById(int id) {
    return getOrRefreshToken()
        .flatMap(token -> webClient
            .get()
            .uri("/api/desastres/{id}", id)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals,
                clientResponse -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Desastre " + id + " not found")))
            .bodyToMono(HechoProxyInputDTO.class)
        );
  }
  /* FIN API CATEDRA */

  
  /* API PROPIA */
  public Mono<List<HechoProxyOutputDTO>> obtenerHechosDeOtraInstancia(String urlBase, Map<String, String> filtros) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(urlBase + "/hechos");
    filtros.forEach(uriBuilder::queryParam); // agrega los filtros como query params

    WebClient instanciaClient = webClientBuilder.baseUrl(urlBase).build();

    return instanciaClient.get()
        .uri(uriBuilder.toUriString())
        .retrieve()
        .bodyToFlux(HechoProxyOutputDTO.class)
        .collectList();
  }

  public Mono<List<ColeccionProxyOutputDTO>> obtenerColeccionesDeOtraInstancia(String urlBase) {
    WebClient instanciaClient = webClientBuilder.baseUrl(urlBase).build();

    return instanciaClient.get()
        .uri("/colecciones")
        .retrieve()
        .bodyToFlux(ColeccionProxyOutputDTO.class)
        .collectList();
  }

  public Mono<List<HechoProxyOutputDTO>> obtenerHechosDeColeccionDeOtraInstancia(String urlBase, String idColeccion, Map<String, String> filtros) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(urlBase + "/colecciones/" + idColeccion + "/hechos");
    filtros.forEach(uriBuilder::queryParam);

    WebClient instanciaClient = webClientBuilder.baseUrl(urlBase).build();

    return instanciaClient.get()
        .uri(uriBuilder.toUriString())
        .retrieve()
        .bodyToFlux(HechoProxyOutputDTO.class)
        .collectList();
  }

  public Mono<SolicitudEliminacionProxyOutputDTO> crearSolicitudEnOtraInstancia(String urlBase, SolicitudEliminacionProxyInputDTO solicitud) {
    WebClient instanciaClient = webClientBuilder.baseUrl(urlBase).build();

    return instanciaClient.post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        .bodyToMono(SolicitudEliminacionProxyOutputDTO.class);
  }

  /* FIN API PROPIA */

}
