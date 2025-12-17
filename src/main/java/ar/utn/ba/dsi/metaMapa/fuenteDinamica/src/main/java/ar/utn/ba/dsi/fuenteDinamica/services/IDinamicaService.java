package ar.utn.ba.dsi.fuenteDinamica.services;

import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.HechoDinamicaInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.HechoDinamicaOutputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.SolicitudEliminacion;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IDinamicaService {

  List<HechoDinamicaOutputDTO> enviarAAgregador();
  List<HechoDinamicaOutputDTO> buscarTodas();
  HechoDinamicaOutputDTO buscarPorId(long id);
  HechoDinamicaOutputDTO crear(HechoDinamicaInputDTO hechoInput, MultipartFile archivo, String userId);

  List<HechoDinamicaOutputDTO> buscarPendientesRevision();
  void aprobarHecho(long id);
  void rechazarHecho(long id);
  void asignarEtiqueta(long id, String etiqueta);
  void crearSolicitudEliminacion(long idHecho, String motivo, String solicitanteId);
  List<SolicitudOutputDTO> listarSolicitudesEliminacionPendientes();
  void resolverSolicitudEliminacion(long idSolicitud, boolean aceptar);
  List<HechoDinamicaOutputDTO> buscarHechosPorUsuario(String userId);
  void ocultarHecho(String titulo);
  HechoDinamicaOutputDTO obtenerHechoPorIdEdicion(Long idEdicion);
  void aceptarConSugerencia(Long id, String sugerencia, String adminId);
}
