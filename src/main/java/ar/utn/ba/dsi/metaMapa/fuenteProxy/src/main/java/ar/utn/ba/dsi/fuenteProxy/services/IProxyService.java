package ar.utn.ba.dsi.fuenteProxy.services;

import ar.utn.ba.dsi.fuenteProxy.models.dtos.external.AuthResponseDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.input.HechoProxyInputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.input.SolicitudEliminacionProxyInputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.ColeccionProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.HechoProxyOutputDTO;
import ar.utn.ba.dsi.fuenteProxy.models.dtos.output.SolicitudEliminacionProxyOutputDTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface IProxyService {
  public Mono<HechoProxyInputDTO> getDesastresById(int id);
  public Mono<List<HechoProxyOutputDTO>> getDesastres();
  public Mono<List<HechoProxyOutputDTO>> obtenerHechosDeOtraInstancia(String urlBase, Map<String, String> filtros);
  Mono<List<ColeccionProxyOutputDTO>> obtenerColeccionesDeOtraInstancia(String urlBase);
  Mono<List<HechoProxyOutputDTO>> obtenerHechosDeColeccionDeOtraInstancia(String urlBase, String idColeccion, Map<String, String> filtros);
  public Mono<SolicitudEliminacionProxyOutputDTO> crearSolicitudEnOtraInstancia(String urlBase, SolicitudEliminacionProxyInputDTO solicitud);
  //void ejecutarPruebaLogin();
  Mono<AuthResponseDTO> authenticateAndGetToken();
}
