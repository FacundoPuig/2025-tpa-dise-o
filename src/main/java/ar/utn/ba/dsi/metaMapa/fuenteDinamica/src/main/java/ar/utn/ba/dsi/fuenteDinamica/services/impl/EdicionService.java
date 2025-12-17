package ar.utn.ba.dsi.fuenteDinamica.services.impl;

import ar.utn.ba.dsi.fuenteDinamica.exceptions.ResourceNotFoundException;
import ar.utn.ba.dsi.fuenteDinamica.exceptions.UnauthorizedException;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.EdicionInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.EdicionOutputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.*;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.ICategoriaRepository;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IEdicionRepository;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IHechoDinamicaRepository;
import ar.utn.ba.dsi.fuenteDinamica.services.IEdicionService;
import ar.utn.ba.dsi.fuenteDinamica.services.IFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Categoria;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EdicionService implements IEdicionService {

  @Autowired
  private IEdicionRepository edicionRepository;
  @Autowired
  private IHechoDinamicaRepository hechoDinamicaRepository;
  @Autowired
  private IFileStorageService fileStorageService;
  @Autowired
  private ICategoriaRepository categoriaRepository;

  @Override
  @Transactional
  public void crearEdicion(long idHecho, EdicionInputDTO edicionInput, String editorId, MultipartFile archivo) {
    Hecho hechoOriginal = hechoDinamicaRepository.findById(idHecho)
        .orElseThrow(() -> new ResourceNotFoundException("Hecho no encontrado con ID: " + idHecho));

    if (hechoOriginal.getVisualizadorCreadorId() == null || !hechoOriginal.getVisualizadorCreadorId().equals(editorId)) {
      throw new UnauthorizedException("Solo el autor original puede proponer una edición para este hecho.");
    }

    if (!hechoOriginal.fueCreadoHaceMenosDeDias(7)) {
      throw new UnauthorizedException("El plazo para editar este hecho ha expirado (7 días).");
    }

    if (edicionRepository.findPendingByHechoId(hechoOriginal.getId()).isPresent()) {
      throw new IllegalArgumentException("Ya existe una edición pendiente para este hecho.");
    }

    Edicion nuevaEdicion = new Edicion();
    nuevaEdicion.setIdHechoOriginal(hechoOriginal);

    nuevaEdicion.setTituloPropuesto(edicionInput.getTituloPropuesto());
    nuevaEdicion.setDescripcionPropuesta(edicionInput.getDescripcionPropuesta());

    if (edicionInput.getCategoriaPropuesta() != null) {
      String nombreCat = edicionInput.getCategoriaPropuesta();
      // Buscamos la categoría. Si no existe, la creamos Y LA GUARDAMOS (save) para tener ID.
      Categoria categoria = categoriaRepository.findByNombre(nombreCat)
          .orElseGet(() -> categoriaRepository.save(new Categoria(nombreCat)));

      nuevaEdicion.setCategoriaPropuesta(categoria);
    }

    nuevaEdicion.setLatitudPropuesta(edicionInput.getLatitudPropuesta());
    nuevaEdicion.setLongitudPropuesta(edicionInput.getLongitudPropuesta());
    nuevaEdicion.setFechaAcontecimientoPropuesta(edicionInput.getFechaAcontecimientoPropuesta());


    if (archivo != null && !archivo.isEmpty()) {
      String nombreArchivoGuardado = fileStorageService.guardar(archivo);
      nuevaEdicion.setContenidoMultimediaPropuesto(nombreArchivoGuardado);
    }

    nuevaEdicion.setVisualizadorEditorId(editorId);
    nuevaEdicion.setFechaEdicion(LocalDate.now());
    nuevaEdicion.setEstado(EstadoEdicion.PENDIENTE);

    edicionRepository.save(nuevaEdicion);
  }

  @Override
  public List<EdicionOutputDTO> buscarListadeHechosPendientesEdicion(String revisorId) {
    // TODO: Validar que revisorId sea ADMIN si es necesario
    List<Edicion> edicionesPendientes = edicionRepository.findByEstado(EstadoEdicion.PENDIENTE);
    return edicionesPendientes.stream().map(this::convertirAEdicionPendienteOutputDTO).collect(Collectors.toList());
  }

  @Override
  public EdicionOutputDTO verEdicionPendiente(long idEdicion, String revisorId) {
    Edicion edicion = edicionRepository.findById(idEdicion)
        .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
    return convertirAEdicionPendienteOutputDTO(edicion);
  }

  @Override
  @Transactional
  public void aceptarEdicion(long idEdicion, String revisorId) {
    // 1. Buscar la edición
    Edicion edicion = edicionRepository.findById(idEdicion)
        .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));

    // 2. Marcar edición como aprobada
    edicion.setEstado(EstadoEdicion.APROBADA);
    edicionRepository.save(edicion);

    Hecho hechoViejo = edicion.getIdHechoOriginal();

    // 3. CORRECCIÓN TRAZABILIDAD: Crear instancia NUEVA
    Hecho hechoNuevo = new Hecho();

    // Copiar datos base que no cambian (o lógica de negocio específica)
    hechoNuevo.setOrigen(hechoViejo.getOrigen());
    hechoNuevo.setVisualizadorCreadorId(hechoViejo.getVisualizadorCreadorId());
    hechoNuevo.setFechaCarga(LocalDate.now());
    hechoNuevo.setEstadoRevision(EstadoRevision.ACEPTADO); // Ya nace aprobado por ser edición aceptada
    hechoNuevo.setEnviado(false); // Para que el Agregador lo detecte

    // Aplicar cambios: Si es null en edición, mantengo el viejo. Si no, uso el nuevo.
    hechoNuevo.setTitulo(edicion.getTituloPropuesto() != null ? edicion.getTituloPropuesto() : hechoViejo.getTitulo());
    hechoNuevo.setDescripcion(edicion.getDescripcionPropuesta() != null ? edicion.getDescripcionPropuesta() : hechoViejo.getDescripcion());
    hechoNuevo.setCategoria(edicion.getCategoriaPropuesta() != null ? edicion.getCategoriaPropuesta() : hechoViejo.getCategoria());
    hechoNuevo.setFechaAcontecimiento(edicion.getFechaAcontecimientoPropuesta() != null ? edicion.getFechaAcontecimientoPropuesta() : hechoViejo.getFechaAcontecimiento());

    // Manejo de Ubicación (Entity separada)
    Double lat = edicion.getLatitudPropuesta() != null ? edicion.getLatitudPropuesta() : hechoViejo.getUbicacion().getLatitud();
    Double lon = edicion.getLongitudPropuesta() != null ? edicion.getLongitudPropuesta() : hechoViejo.getUbicacion().getLongitud();
    hechoNuevo.setUbicacion(new Ubicacion(lat, lon));

    hechoNuevo.setContenidoMultimedia(edicion.getContenidoMultimediaPropuesto() != null ? edicion.getContenidoMultimediaPropuesto() : hechoViejo.getContenidoMultimedia());

    // 4. Guardar hecho nuevo
    hechoDinamicaRepository.save(hechoNuevo);

    // 5. "Eliminar" lógicamente el hecho viejo (visible = false) [cite: 19]
    hechoViejo.ocultar();
    hechoDinamicaRepository.save(hechoViejo);
    // 6. avisar a Agregador (por el campo enviado=false)


  }

  @Override
  public void rechazarEdicion(long idEdicion, String revisorId) {
    Edicion edicion = edicionRepository.findById(idEdicion)
        .orElseThrow(() -> new ResourceNotFoundException("Edición no encontrada"));
    edicion.setEstado(EstadoEdicion.RECHAZADA);
    edicionRepository.save(edicion);
  }

  @Override
  public List<EdicionOutputDTO> buscarPorUsuario(String userId) {
    return edicionRepository.findByVisualizadorEditorId(userId)
        .stream()
        .map(this::convertirAEdicionPendienteOutputDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<EdicionOutputDTO> buscarTodas() {
    return edicionRepository.findAll().stream()
        .map(this::convertirAEdicionPendienteOutputDTO)
        .collect(Collectors.toList());
  }

  private EdicionOutputDTO convertirAEdicionPendienteOutputDTO(Edicion edicion) {
    EdicionOutputDTO dto = new EdicionOutputDTO();
    dto.setId(edicion.getId());
    dto.setIdHechoOriginal(edicion.getIdHechoOriginal().getId());
    dto.setTituloPropuesto(edicion.getTituloPropuesto());
    dto.setDescripcionPropuesta(edicion.getDescripcionPropuesta());

    if (edicion.getCategoriaPropuesta() != null) {
      dto.setCategoriaPropuestaId(edicion.getCategoriaPropuesta().getId());
      dto.setCategoriaPropuestaNombre(edicion.getCategoriaPropuesta().getNombre());
    }

    dto.setLatitudPropuesta(edicion.getLatitudPropuesta());
    dto.setLongitudPropuesta(edicion.getLongitudPropuesta());
    dto.setFechaAcontecimientoPropuesta(edicion.getFechaAcontecimientoPropuesta());
    dto.setContenidoMultimediaPropuesto(edicion.getContenidoMultimediaPropuesto());
    dto.setVisualizadorEditor(edicion.getVisualizadorEditorId());
    dto.setFechaEdicion(edicion.getFechaEdicion());
    dto.setEstado(edicion.getEstado());
    return dto;
  }
}