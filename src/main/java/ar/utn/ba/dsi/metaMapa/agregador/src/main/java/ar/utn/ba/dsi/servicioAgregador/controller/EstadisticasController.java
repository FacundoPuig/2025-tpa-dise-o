/*   ------ se hace desde estadisticas directo al front


package ar.utn.ba.dsi.servicioAgregador.controller;


import ar.utn.ba.dsi.servicioAgregador.models.dtos.CategoriaMasReportadaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.HoraHechosPorCategoriaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.ProvinciaHechosPorCategoriaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.ProvinciaMasHechosPorColeccionDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.SolicitudSpamDTO;
import ar.utn.ba.dsi.servicioAgregador.services.impl.EstadisticaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estadisticas")
@CrossOrigin(origins = "${frontend.client.url}")
public class EstadisticasController {

  @Autowired
  private EstadisticaClient estadisticaClient;

  @GetMapping("/{handleId}/provincia-mas-hechos")
  public ProvinciaMasHechosPorColeccionDTO getProvinciaConMasHechosSegunLaColeccion(@PathVariable String handleId) {
    return estadisticaClient.getProvinciaConMasHechos(handleId);
  }

  @GetMapping("/categoria-mas-reportada")
  public CategoriaMasReportadaDTO getCategoriaMasReportada(){
    return estadisticaClient.getCategoriaConMasHechos();
  }

  @GetMapping("/provincia-mas-hechos-por-categoria")
  public ProvinciaHechosPorCategoriaDTO getProvinciaConMasHechos(@RequestParam String categoria){
    return estadisticaClient.getProvinciaConMasHechosSegunCategoria(categoria);
  }

  @GetMapping("/hora-mas-hechos")
  public HoraHechosPorCategoriaDTO getHoraConMasHechos(@RequestParam String categoria){
    return estadisticaClient.getHoraConMasHechosSegunCategoria(categoria);
  }

  @GetMapping("/solicitudes-spam")
  public SolicitudSpamDTO getCantidadDeSpam(){
    return estadisticaClient.getCantidadDeSolicitudesSpam();
  }

  //Para exportar
  @GetMapping("/exportar/todas")
  public byte[] exportarReporteCompletoZIP() {
    return estadisticaClient.exportarReporteCompletoZIP();
  }

  @GetMapping("/exportar/provincia_por_coleccion")
  public String exportarProvinciaConMasHechosCSV() {
    return estadisticaClient.exportarProvinciaConMasHechosCSV();
  }

  @GetMapping("/exportar/categoria_mas_hechos")
  public String exportarCategoriasConMasHechosCSV() {
    return estadisticaClient.exportarCategoriasConMasHechosCSV();
  }

  @GetMapping("/exportar/provincia_por_categoria")
  public String exportarProvinciaPorCategoriaCSV() {
    return estadisticaClient.exportarProvinciaPorCategoriaCSV();
  }

  @GetMapping("/exportar/hora_por_categoria")
  public String exportarCategoriasConMasHechosEnUnaHoraCSV() {
    return estadisticaClient.exportarCategoriasConMasHechosEnUnaHoraCSV();
  }

  @GetMapping("/exportar/spam")
  public String exportarCantidadSolicitudesSpam(){
    return estadisticaClient.exportarCantidadSolicitudesSpam();
  }
}*/
