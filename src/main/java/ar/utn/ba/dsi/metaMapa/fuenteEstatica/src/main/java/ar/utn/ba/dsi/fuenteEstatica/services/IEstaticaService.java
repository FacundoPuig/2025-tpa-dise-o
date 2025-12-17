package ar.utn.ba.dsi.fuenteEstatica.services;
import ar.utn.ba.dsi.fuenteEstatica.models.dtos.output.HechoEstaticaOutputDTO;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.solicitudes.SolicitudEliminacion;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface IEstaticaService {
  void cargarCSV(MultipartFile archivoCSV);
  List<HechoEstaticaOutputDTO> enviarHechosAAgregador();
  void ocultarHecho(String titulo);
  void guardarSolicitudEliminacion(SolicitudEliminacion solicitudEliminacion);
}
