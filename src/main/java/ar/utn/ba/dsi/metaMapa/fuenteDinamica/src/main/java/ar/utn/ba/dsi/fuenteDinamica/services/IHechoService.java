package ar.utn.ba.dsi.fuenteDinamica.services;

import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.HechoDinamicaOutputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface IHechoService {
  public HechoDinamicaOutputDTO hechoOutputDTO(Hecho hecho);

 void guardarContenidoMultimedia(long hechoId, MultipartFile archivo);

  Resource cargarComoRecurso(String nombreArchivo);
}
