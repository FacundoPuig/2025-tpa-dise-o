package ar.utn.ba.dsi.fuenteDinamica.services.impl;

import ar.utn.ba.dsi.fuenteDinamica.exceptions.ResourceNotFoundException;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.HechoDinamicaOutputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Etiqueta;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IHechoDinamicaRepository;
import ar.utn.ba.dsi.fuenteDinamica.services.IFileStorageService;
import ar.utn.ba.dsi.fuenteDinamica.services.IHechoService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HechoService implements IHechoService {

  private final IHechoDinamicaRepository hechoRepository;
  private final IFileStorageService fileStorageService;

  public HechoService(IHechoDinamicaRepository hechoRepository, IFileStorageService fileStorageService) {
    this.hechoRepository = hechoRepository;
    this.fileStorageService = fileStorageService;
  }


  @Override
  public HechoDinamicaOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoDinamicaOutputDTO hechoOutput = new HechoDinamicaOutputDTO();

    hechoOutput.setId(hecho.getId());
    hechoOutput.setTitulo(hecho.getTitulo());
    hechoOutput.setDescripcion(hecho.getDescripcion());

    // Mapeo de Categoría
    if (hecho.getCategoria() != null) {
      hechoOutput.setCategoria(hecho.getCategoria().getNombre());
    }

    // Mapeo de Ubicación (Aplanado)
    if (hecho.getUbicacion() != null) {
      hechoOutput.setLatitud(hecho.getUbicacion().getLatitud());
      hechoOutput.setLongitud(hecho.getUbicacion().getLongitud());
    }

    hechoOutput.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoOutput.setFechaCarga(hecho.getFechaCarga());
    hechoOutput.setContenidoMultimedia(hecho.getContenidoMultimedia());

    // Mapeo de Origen (Fix error: cannot find symbol setNombreOrigen)
    if (hecho.getOrigen() != null) {
      hechoOutput.setNombreOrigen(hecho.getOrigen().getNombre());
      if (hecho.getOrigen().getProvieneDe() != null) {
        hechoOutput.setProvieneDeOrigen(hecho.getOrigen().getProvieneDe().name());
      }
    }

    // Mapeo de Etiquetas (Fix error: incompatible types)
    if (hecho.getEtiquetas() != null) {
      List<String> nombresEtiquetas = hecho.getEtiquetas().stream()
          .map(etiqueta -> etiqueta.getNombre())
          .collect(Collectors.toList());
      hechoOutput.setEtiquetas(nombresEtiquetas);
    } else {
      hechoOutput.setEtiquetas(new ArrayList<>());
    }

    if (hecho.getEstadoRevision() != null) {
      hechoOutput.setEstado(hecho.getEstadoRevision().name());
    }

    return hechoOutput;
  }

  @Override
  public void guardarContenidoMultimedia(long hechoId, MultipartFile archivo) {
    // JPA devuelve Optional, así que usamos orElseThrow
    Hecho hecho = hechoRepository.findById(hechoId)
        .orElseThrow(() -> new ResourceNotFoundException("Hecho no encontrado con ID: " + hechoId));

    if (hecho.getContenidoMultimedia() != null && !hecho.getContenidoMultimedia().isEmpty()) {
      fileStorageService.eliminar(hecho.getContenidoMultimedia());
    }

    String nombreArchivoGuardado = fileStorageService.guardar(archivo);

    hecho.setContenidoMultimedia(nombreArchivoGuardado);
    hechoRepository.save(hecho);
  }

  @Override
  public Resource cargarComoRecurso(String nombreArchivo) {
    return fileStorageService.cargarComoRecurso(nombreArchivo);
  }
}