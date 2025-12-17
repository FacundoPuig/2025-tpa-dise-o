/*package ar.utn.ba.dsi.servicioAgregador.services.impl;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.CategoriaMasReportadaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.HoraHechosPorCategoriaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.ProvinciaHechosPorCategoriaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.ProvinciaMasHechosPorColeccionDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.SolicitudSpamDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EstadisticaClient {
  private final WebClient webClient;

  public EstadisticaClient(WebClient.Builder builder, @Value("${estadistica.ruta}") String rutaEstadistica) {
    this.webClient = builder.baseUrl(rutaEstadistica).build();
  }

  // 1) Busca, a partir de una coleccion, que prov tiene mas hechos
  public ProvinciaMasHechosPorColeccionDTO getProvinciaConMasHechos(String handleId) {
    return webClient.get()
        .uri("/estadisticas/{handleId}/provincia-mas-hechos")
        .retrieve()
        .bodyToMono(ProvinciaMasHechosPorColeccionDTO.class)
        .block();
  }

  // 2) categoria con mas hechos
  public CategoriaMasReportadaDTO getCategoriaConMasHechos() {
    return webClient.get()
        .uri("/estadisticas/categoria-mas-hechos")
        .retrieve()
        .bodyToMono(CategoriaMasReportadaDTO.class)
        .block();
  }

  //3) De cierta Categoria, provincia con mas hechos
  public ProvinciaHechosPorCategoriaDTO getProvinciaConMasHechosSegunCategoria(String categoria) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/estadisticas/provincia-mas-hechos-por-categoria")
            .queryParam("categoria", categoria)
            .build())
        .retrieve()
        .bodyToMono(ProvinciaHechosPorCategoriaDTO.class)
        .block();
  }

  //4) de una categoria, a q hora ocurren la > cant de hechos
  public HoraHechosPorCategoriaDTO getHoraConMasHechosSegunCategoria(String categoria) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/estadisticas/hechos-por-hora")
            .queryParam("categoria", categoria)
            .build())
        .retrieve()
        .bodyToMono(HoraHechosPorCategoriaDTO.class)
        .block();
  }

  //5) devulve cant de spam
  public SolicitudSpamDTO getCantidadDeSolicitudesSpam(){
    return webClient.get()
        .uri("/estadisticas/solicitudes_spam")
        .retrieve()
        .bodyToMono(SolicitudSpamDTO.class)
        .block();
  }

  //-----para la parte de exportar----

  // Todas
  public byte[] exportarReporteCompletoZIP() {
    return webClient.get()
        .uri("/estadisticas/exportar/todas") // Llama al endpoint de ZIP
        .retrieve()
        .bodyToMono(byte[].class)
        .block();
  }

  // 1)
  public String exportarProvinciaConMasHechosCSV() {
    return webClient.get()
        .uri("/estadisticas/exportar/provincia_por_coleccion")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }
  // 2)
  public String exportarCategoriasConMasHechosCSV() {
    return webClient.get()
        .uri("/estadisticas/exportar/categoria_mas_hechos")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }
  // 3)
  public String exportarProvinciaPorCategoriaCSV() {
    return webClient.get()
        .uri("/estadisticas/exportar/provincia_por_categoria")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  // 4)
  public String exportarCategoriasConMasHechosEnUnaHoraCSV(){
    return webClient.get()
        .uri("/estadisticas/exportar/hora_por_categoria")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  // 5)
  public String exportarCantidadSolicitudesSpam(){
    return webClient.get()
        .uri("/estadisticas/exportar/spam")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

}*/
