package ar.utn.ba.dsi.servicioAgregador.services;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.SolicitudEliminacionAgregadorInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.RegistroEstadoOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.SolicitudEliminacionAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.Estados;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.RegistroCambioEstado;
import org.springframework.security.core.Authentication;


import java.util.List;

public interface ISolicitudEliminacionService {
  List<SolicitudEliminacionAgregadorOutputDTO> buscarTodas();
  SolicitudEliminacionAgregadorOutputDTO buscarPorId(Integer id);
  SolicitudEliminacionAgregadorOutputDTO crear(SolicitudEliminacionAgregadorInputDTO input, String userId);
  void eliminar(Integer id);
  List<SolicitudEliminacionAgregadorOutputDTO> mostrarSolicitudes();
  SolicitudEliminacionAgregadorOutputDTO evaluarSolicitud(Integer nroSolicitud, boolean aceptado, Authentication authentication);
  List<SolicitudEliminacionAgregadorOutputDTO> buscarPorSolicitante(String solicitanteId);
  List<RegistroEstadoOutputDTO> obtenerTodosRegistroCambioEstados();
}
