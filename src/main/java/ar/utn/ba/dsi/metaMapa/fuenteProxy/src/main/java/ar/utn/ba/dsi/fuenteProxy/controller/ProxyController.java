package ar.utn.ba.dsi.fuenteProxy.controller;

import ar.utn.ba.dsi.fuenteProxy.models.dtos.external.AuthResponseDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.input.HechoProxyInputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.input.SolicitudEliminacionProxyInputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.ColeccionProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.HechoProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.SolicitudEliminacionProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.services.IProxyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/proxy")
@CrossOrigin(origins = "${agregador.ruta}")
public class ProxyController {
  @Autowired
  private IProxyService proxyService;

  /* API PROPIA */ /* PROXY NO USA REPOS ASI QUE ESTAN MAL! LAS QUE ESTAN BIEN SON LAS QUE ESTAN ABAJO DE TODO*/
  /*@GetMapping("/hechos")
  public List<Hecho> obtenerHechosDeAPI() {
    return proxyService.obtenerHechosDeAPI();
  }

  @GetMapping("/colecciones/{id}/hechos")
  private List<HechoProxyOutputDTO> buscarHechosPorId(@RequestParam String instanciaUrl, @PathVariable String id, @PathVariable Map<String, String> filtros){
    return proxyService.obtenerHechosDeColeccionDeOtraInstancia(instanciaUrl, id, filtros).block();
  }*/

  /* API CATEDRA */
  @GetMapping("/desastres")
  public Mono<List<HechoProxyOutputDTO>> listarDesastres() { // <-- Cambio de tipo
    return proxyService.getDesastres();
  }

  @PostMapping("/logearse")
  public Mono<AuthResponseDTO> ejecutarPruebaLogin(){
    return proxyService.authenticateAndGetToken();
  }

  @GetMapping("/desastres/{id}")
  public Mono<HechoProxyInputDTO> obtenerDesastre(@PathVariable("id") int id) {
    return proxyService.getDesastresById(id);
  }
  /* FIN API CATEDRA */

  /* API PROPIA - CORREGIDA PARA SPRING BOOT 3 */

  @GetMapping("/hechos")
  public Mono<List<HechoProxyOutputDTO>> consultarOtraInstancia(
      @RequestParam("instanciaUrl") String instanciaUrl, // <--- AGREGADO ("nombre")
      @RequestParam(required = false) Map<String, String> filtros
  ) {
    return proxyService.obtenerHechosDeOtraInstancia(instanciaUrl, filtros);
  }

  @GetMapping("/colecciones")
  public Mono<List<ColeccionProxyOutputDTO>> consultarColeccionesExternas(
      @RequestParam("instanciaUrl") String instanciaUrl // <--- AGREGADO ("nombre")
  ) {
    return proxyService.obtenerColeccionesDeOtraInstancia(instanciaUrl);
  }

  @GetMapping("/colecciones/{id}/hechos")
  public Mono<List<HechoProxyOutputDTO>> consultarHechosDeColeccionExterna(
      @PathVariable("id") String id, // <--- AGREGADO ("id") (por si acaso)
      @RequestParam("instanciaUrl") String instanciaUrl, // <--- AGREGADO ("nombre")
      @RequestParam(required = false) Map<String, String> filtros
  ) {
    return proxyService.obtenerHechosDeColeccionDeOtraInstancia(instanciaUrl, id, filtros);
  }

  @PostMapping("/solicitudes")
  public Mono<SolicitudEliminacionProxyOutputDTO> enviarSolicitudAInstanciaExterna(
      @RequestParam("instanciaUrl") String instanciaUrl, // <--- AGREGADO ("nombre")
      @RequestBody SolicitudEliminacionProxyInputDTO solicitud
  ) {
    return proxyService.crearSolicitudEnOtraInstancia(instanciaUrl, solicitud);
  }
  /* FIN API PROPIA */
}
