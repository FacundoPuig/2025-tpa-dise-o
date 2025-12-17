package ar.utn.ba.dsi.servicioAgregador.services.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class VisualizadorServiceClient {
  private final WebClient webClient;

  // Usaremos la inyección de la ruta dinámica como placeholder para la ruta de Usuarios.
  public VisualizadorServiceClient(WebClient.Builder builder, @Value("${usuarios.ruta}") String rutaUsuarios) {
    this.webClient = builder.baseUrl(rutaUsuarios).build();
  }

  /**
   * Llama al Servicio de Usuarios para verificar que el email corresponda al ID.
   * @return True si el Servicio de Usuarios retorna 2xx, False si retorna 403 Forbidden.
   */
  public boolean verificarIdDeUsuario(String email, Long visualizadorId) { // <-- Devuelve boolean
    System.out.println("Verificando que el ID " + visualizadorId + " corresponda al email " + email);
    try {
      this.webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/verify-id")
              .queryParam("email", email)
              .queryParam("visualizadorId", visualizadorId)
              .build())
          .retrieve()
          // Capturamos el 403, que es la única excepción esperada por negocio.
          .onStatus(HttpStatus.FORBIDDEN::equals,
              response -> Mono.error(new WebClientResponseException(HttpStatus.FORBIDDEN.value(), "ID Mismatch", null, null, null)))
          .toBodilessEntity()
          .block();

      return true; // Si no hay excepción, es 200 OK

    } catch (WebClientResponseException e) {
      System.out.println("Error al verificar ID de usuario: " + e.getStatusCode());
      if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
        return false; // El 403 significa que el ID no es del usuario
      }
      // Relanzar otros errores (5xx, fallos de conexión)
      throw new RuntimeException("Error de servicio al verificar ID: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException("Error de comunicación con Servicio Usuarios.", e);
    }
  }


}
