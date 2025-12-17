package ar.utn.ba.dsi.fuenteDinamica.services;

import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.EdicionInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.EdicionOutputDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface IEdicionService {
  void crearEdicion(long idHecho, EdicionInputDTO edicionInput, String editorId, MultipartFile archivo);
  List<EdicionOutputDTO> buscarListadeHechosPendientesEdicion(String revisorId);
  EdicionOutputDTO verEdicionPendiente(long idEdicion, String revisorId);
  void aceptarEdicion(long idEdicion, String revisorId);
  void rechazarEdicion(long idEdicion, String revisorId);
  List<EdicionOutputDTO> buscarPorUsuario(String userId);
  List<EdicionOutputDTO> buscarTodas();
}