package ar.utn.ba.dsi.fuenteDinamica.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {

  String guardar(MultipartFile archivo);
  Resource cargarComoRecurso(String nombreArchivo);
  void eliminar(String nombreArchivo);
}
