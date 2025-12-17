package ar.utn.ba.dsi.fuenteEstatica.services.impl;

import ar.utn.ba.dsi.fuenteEstatica.models.dtos.output.HechoEstaticaOutputDTO;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.fileReader.FileReader;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.EstadoRevision;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Etiqueta;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.solicitudes.SolicitudEliminacion;
import ar.utn.ba.dsi.fuenteEstatica.models.repositories.IHechoEstaticaRepository;
import ar.utn.ba.dsi.fuenteEstatica.models.repositories.ISolicitudEliminacionEstaticaRepository;
import ar.utn.ba.dsi.fuenteEstatica.services.IEstaticaService;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Hecho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class EstaticaService implements IEstaticaService {

  @Autowired
  private IHechoEstaticaRepository hechoAgregacionRepository;

  @Autowired
  private ISolicitudEliminacionEstaticaRepository solicitudEliminacionRepository;

  private final FileReader fr = new FileReader("${fuente.carpeta.entrada}",
      "${fuente.carpeta.salida}");

  public HechoEstaticaOutputDTO crearHechoOutputDTO(Hecho hecho) {
    HechoEstaticaOutputDTO hechoOutput = new HechoEstaticaOutputDTO();
    hechoOutput.setTitulo(hecho.getTitulo());
    hechoOutput.setDescripcion(hecho.getDescripcion());
    hechoOutput.setCategoria(hecho.getCategoria().getNombre());
    hechoOutput.setLatitud(hecho.getUbicacion().getLatitud());
    hechoOutput.setLongitud(hecho.getUbicacion().getLongitud());
    hechoOutput.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoOutput.setFechaCarga(hecho.getFechaCarga());
    hechoOutput.setContenidoMultimedia(hecho.getContenidoMultimedia());
    hechoOutput.setNombreOrigen(hecho.getOrigen().getNombre());
    //hechoOutput.setProvincia(hecho.getProvincia());
    hechoOutput.setProvieneDeOrigen(hecho.getOrigen().getProvieneDe().toString());
    hechoOutput.setEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getNombre).toList());

    return hechoOutput;
  }

  public void cargarCSV(MultipartFile archivoCSV) {
    String nombreArchivo = archivoCSV.getOriginalFilename();

    if (fr.archivoYaFueProcesado(nombreArchivo)) {
      throw new IllegalArgumentException("El archivo '" + archivoCSV + "' ya fue procesado anteriormente.");
    }

    fr.cargarCSV(archivoCSV);
    leerCSV();
    System.out.println("✅ Hechos cargados desde el CSV y guardados en la base de datos.");
  }

  private void leerCSV() {
    List<Hecho> ListaDeHechos = fr.leerHechosDesdeCSV();
    //guardar en base de datos
    hechoAgregacionRepository.saveAll(ListaDeHechos);
  }

  public List<HechoEstaticaOutputDTO> enviarHechosAAgregador() {

    List<Hecho> hechosParaEnviar = hechoAgregacionRepository.findByVisibleTrueAndEnviadoFalse();

    if (hechosParaEnviar.isEmpty()) {
      return List.of();
    }

    //marcar los hechos como enviados (enviado = true) para q no se mande en la prox
    hechosParaEnviar.forEach(hecho -> hecho.setEnviado(true));
    hechoAgregacionRepository.saveAll(hechosParaEnviar);

    return hechosParaEnviar.stream()
        .map(this::crearHechoOutputDTO)
        .toList();
  }


  public void ocultarHecho(String titulo) {
    Hecho hecho = hechoAgregacionRepository.findByTitulo(titulo);
    if (hecho != null) {
      hecho.ocultar();
      hecho.setEstadoRevision(EstadoRevision.RECHAZADO);
      hechoAgregacionRepository.save(hecho);
      System.out.println("Hecho con ID " + titulo + " ha sido ocultado.");
    } else {
      System.out.println("No se encontró el hecho con ID: " + titulo);
    }
  }

  public void guardarSolicitudEliminacion(SolicitudEliminacion solicitudEliminacion) {
    System.out.println("Guardando solicitud de eliminación para el hecho ID: " + solicitudEliminacion.getUnHecho().getId());
    solicitudEliminacionRepository.save(solicitudEliminacion);
  }
}