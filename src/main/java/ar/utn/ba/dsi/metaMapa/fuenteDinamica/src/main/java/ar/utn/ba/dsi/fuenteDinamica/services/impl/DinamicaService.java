package ar.utn.ba.dsi.fuenteDinamica.services.impl;

import ar.utn.ba.dsi.fuenteDinamica.exceptions.NotFoundException;
import ar.utn.ba.dsi.fuenteDinamica.exceptions.ResourceNotFoundException;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.HechoDinamicaInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.HechoDinamicaOutputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.*;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.*;
import ar.utn.ba.dsi.fuenteDinamica.services.IDinamicaService;
import ar.utn.ba.dsi.fuenteDinamica.services.IFileStorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class                           DinamicaService implements IDinamicaService {

  @Autowired
  private IHechoDinamicaRepository hechoDinamicaRepository;
  @Autowired
  private IFileStorageService fileStorageService;
  @Autowired
  private IOrigenesRepository origenesRepository;
  @Autowired
  private ISolicitudEliminacionRepository solicitudRepository;
  @Autowired
  private ICategoriaRepository categoriaRepository;
  @Autowired
  private IEdicionRepository edicionRepository;

  @Override
  public HechoDinamicaOutputDTO crear(HechoDinamicaInputDTO hechoInput, MultipartFile archivo, String userId) {

    if (hechoInput.getTitulo() == null || hechoInput.getTitulo().isBlank()) {
      throw new IllegalArgumentException("El título es obligatorio.");
    }
    if (hechoInput.getDescripcion() == null || hechoInput.getDescripcion().isBlank()) {
      throw new IllegalArgumentException("La descripción es obligatoria."); // Opcional, según la regla de negocio
    }
    if (hechoInput.getLatitud() == null || hechoInput.getLongitud() == null) {
      throw new IllegalArgumentException("La ubicación (latitud y longitud) es obligatoria.");
    }
    if (hechoInput.getCategoria() == null || hechoInput.getCategoria().isBlank()) {
      throw new IllegalArgumentException("La categoría es obligatoria.");
    }

    Hecho hecho = new Hecho();
    Origenes origenDinamica = origenesRepository.findByNombre("DINAMICA")
        .orElseGet(() -> origenesRepository.save(new Origenes("DINAMICA")));

    hecho.setOrigen(origenDinamica);
    hecho.setEstadoRevision(EstadoRevision.PENDIENTE);

    hecho.setTitulo(hechoInput.getTitulo());
    hecho.setDescripcion(hechoInput.getDescripcion());

    String nombreCategoria = hechoInput.getCategoria();
    Categoria categoria = categoriaRepository.findByNombre(nombreCategoria)
        .orElseGet(() -> categoriaRepository.save(new Categoria(nombreCategoria)));

    hecho.setCategoria(categoria);
    hecho.setUbicacion(new Ubicacion(hechoInput.getLatitud(), hechoInput.getLongitud()));

    if (archivo != null && !archivo.isEmpty()) {
      String nombreArchivoGuardado = fileStorageService.guardar(archivo);
      hecho.setContenidoMultimedia(nombreArchivoGuardado);
    }
    hecho.setFechaAcontecimiento(hechoInput.getFechaAcontecimiento());
    hecho.setFechaCarga(LocalDate.now());
    hecho.setVisualizadorCreadorId(userId);

    this.hechoDinamicaRepository.save(hecho);
    return this.hechoOutputDTO(hecho);
  }

  @Override
  public List<HechoDinamicaOutputDTO> enviarAAgregador() {

    List<EstadoRevision> estadosAceptados = List.of(
        EstadoRevision.ACEPTADO,
        EstadoRevision.ACEPTADO_CON_SUGERENCIAS
    );

    List<Hecho> hechosParaEnviar = hechoDinamicaRepository //.findByEstadoRevisionAndVisibleTrue(EstadoRevision.ACEPTADO);
        .findByVisibleTrueAndEnviadoFalseAndEstadoRevisionIn(estadosAceptados);

    if (hechosParaEnviar.isEmpty()) {
      return List.of();
    }

    // Marcar los hechos como ENVIADO = true para q no se manden en la prox
    hechosParaEnviar.forEach(hecho -> hecho.setEnviado(true));
    hechoDinamicaRepository.saveAll(hechosParaEnviar);

    return hechosParaEnviar.stream()
        .map(this::hechoOutputDTO)
        .collect(Collectors.toList());
  }

  @Override
  public HechoDinamicaOutputDTO buscarPorId(long id) {
    // 1. Buscamos el hecho crudo en la DB (findById estándar trae todo, visible o no)
    Hecho hecho = hechoDinamicaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hecho no encontrado con ID: " + id));

    // 2. LÓGICA DE SEGURIDAD: ¿Quién puede ver esto?
    // Permitimos ver si:
    // A) Es público (visible = true)
    // B) O está PENDIENTE (para que el Admin lo revise)
    // C) O está RECHAZADO (para que el Admin/Creador vea por qué se rechazó)
    // D) O está ACEPTADO CON SUGERENCIAS

    boolean accesible = hecho.isVisible()
        || hecho.getEstadoRevision() == EstadoRevision.PENDIENTE
        || hecho.getEstadoRevision() == EstadoRevision.RECHAZADO
        || hecho.getEstadoRevision() == EstadoRevision.ACEPTADO_CON_SUGERENCIAS;

    if (accesible) {
      System.out.print("El hecho es accesible. Estado: " + hecho.getEstadoRevision());
      return hechoOutputDTO(hecho); // Convertimos y devolvemos
    }

    // Si no cumple nada de eso (ej: eliminado por spam oculto), ahí sí 404
    throw new ResourceNotFoundException("El hecho no está disponible.");
  }

  @Override
  public List<HechoDinamicaOutputDTO> buscarPendientesRevision() {
    return this.hechoDinamicaRepository
        .findByEstadoRevisionAndVisibleTrue(EstadoRevision.PENDIENTE)
        .stream()
        .map(this::hechoOutputDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<HechoDinamicaOutputDTO> buscarTodas() {
    return hechoDinamicaRepository.findAll().stream()
        .map(this::hechoOutputDTO)
        .collect(Collectors.toList());
  }

  public void ocultarHecho(String titulo) {
    Hecho hecho = hechoDinamicaRepository.findByTitulo(titulo);
    hecho.ocultar(); // Soft delete: lo ocultamos para que no se vea
    hechoDinamicaRepository.save(hecho);
  }

  @Override
  public void aprobarHecho(long id) {
    Hecho hecho = hechoDinamicaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hecho no encontrado con ID: " + id));

    hecho.setEstadoRevision(EstadoRevision.ACEPTADO);
    hechoDinamicaRepository.save(hecho);
  }

  @Override
  public void rechazarHecho(long id) {
    Hecho hecho = hechoDinamicaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hecho no encontrado con ID: " + id));

    hecho.setEstadoRevision(EstadoRevision.RECHAZADO);
    hecho.ocultar(); // Soft delete: lo ocultamos para que no se vea
    hechoDinamicaRepository.save(hecho);
  }

  @Override
  public void asignarEtiqueta(long id, String nombreEtiqueta) {
    Hecho hecho = hechoDinamicaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hecho no encontrado con ID: " + id));

    // Lógica simple: crear etiqueta y agregar (o buscar si ya existe)
    hecho.agregarEtiqueta(nombreEtiqueta, "Etiqueta asignada por administrador");
    hechoDinamicaRepository.save(hecho);
  }

  @Override
  public void crearSolicitudEliminacion(long idHecho, String motivo, String solicitanteId) {
    Hecho hecho = hechoDinamicaRepository.findById(idHecho)
        .orElseThrow(() -> new ResourceNotFoundException("Hecho no encontrado con ID: " + idHecho));

    // Validación básica
    if (motivo == null || motivo.isBlank()) {
      throw new IllegalArgumentException("El motivo de la baja es obligatorio.");
    }

    if (motivo.length() < 500) {
      throw new IllegalArgumentException("La justificación es muy breve (mínimo 500 caracteres requeridos).");
    }

    SolicitudEliminacion solicitud = new SolicitudEliminacion(hecho, motivo, solicitanteId);
    solicitudRepository.save(solicitud);
  }

  @Override
  public List<SolicitudOutputDTO> listarSolicitudesEliminacionPendientes() {
    return solicitudRepository.findByEstado(EstadoRevision.PENDIENTE)
        .stream()
        .map(this::convertirASolicitudDTO)
        .collect(Collectors.toList());
  }

  @Override
  public void resolverSolicitudEliminacion(long idSolicitud, boolean aceptar) {
    SolicitudEliminacion solicitud = solicitudRepository.findById(idSolicitud)
        .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

    if (aceptar) {
      // 1. Aceptamos la solicitud
      solicitud.setEstado(EstadoRevision.ACEPTADO);

      // 2. OCULTAMOS EL HECHO (Acá está la magia del filtrado) [cite: 202, 208, 448]
      Hecho hecho = solicitud.getHecho();
      hecho.ocultar(); // visible = false
      hechoDinamicaRepository.save(hecho);

    } else {
      // Rechazamos la solicitud, el hecho sigue visible
      solicitud.setEstado(EstadoRevision.RECHAZADO);
    }
    solicitudRepository.save(solicitud);
  }

  public List<HechoDinamicaOutputDTO> buscarHechosPorUsuario(String userId) {
    return hechoDinamicaRepository.findByVisualizadorCreadorId(userId)
       .stream()
       .map(this::hechoOutputDTO)
       .collect(Collectors.toList());
  }

  @Override
  public HechoDinamicaOutputDTO obtenerHechoPorIdEdicion(Long idEdicion) {
    Edicion edicion = edicionRepository.findById(idEdicion)
        .orElseThrow(() -> new NotFoundException("No existe la edición con id " + idEdicion));

    Long hechoOriginalId = edicion.getIdHechoOriginal().getId();

    Hecho hecho = hechoDinamicaRepository.findById(hechoOriginalId)
        .orElseThrow(() -> new NotFoundException("No existe el hecho original con id " + hechoOriginalId));

    return this.hechoOutputDTO(hecho);
  }

  @Transactional
  public void aceptarConSugerencia(Long id, String sugerencia, String adminId) {
    Hecho hecho = hechoDinamicaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Hecho no encontrado"));

    hecho.setEstadoRevision(EstadoRevision.ACEPTADO_CON_SUGERENCIAS);
    hecho.setSugerenciaAdmin(sugerencia);
    hechoDinamicaRepository.save(hecho);
  }

  private HechoDinamicaOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoDinamicaOutputDTO dto = new HechoDinamicaOutputDTO();
    dto.setId(hecho.getId());  // ← ID del hecho original
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getCategoria().getNombre());
    dto.setLatitud(hecho.getUbicacion().getLatitud());
    dto.setLongitud(hecho.getUbicacion().getLongitud());
    dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    dto.setFechaCarga(hecho.getFechaCarga());
    dto.setContenidoMultimedia(hecho.getContenidoMultimedia());
    dto.setNombreOrigen(hecho.getOrigen().getNombre());
    dto.setProvieneDeOrigen(hecho.getOrigen().getProvieneDe().name());
    dto.setEtiquetas( hecho.getEtiquetas().stream()
        .map(Etiqueta::getNombre)
        .toList() );
    dto.setEstado(hecho.getEstadoRevision().name());
    dto.setSugerenciaAdmin(hecho.getSugerenciaAdmin());

    // 5. Resultado de Spring Data
    boolean tienePendienteRepository =
        edicionRepository.existsByIdHechoOriginal_IdAndEstado(
            hecho.getId(),
            EstadoEdicion.PENDIENTE
        );

    // 6. Guardar en el DTO
    dto.setTieneEdicionPendiente(tienePendienteRepository);

    return dto;
  }


  private SolicitudOutputDTO convertirASolicitudDTO(SolicitudEliminacion solicitud) {
    SolicitudOutputDTO dto = new SolicitudOutputDTO();
    dto.setId(solicitud.getId());
    dto.setMotivo(solicitud.getMotivo());
    dto.setFechaSolicitud(solicitud.getFechaSolicitud());
    dto.setSolicitanteId(solicitud.getSolicitanteId());
    dto.setEstado(solicitud.getEstado());

    // Mapeamos solo lo necesario del Hecho
    if (solicitud.getHecho() != null) {
      dto.setIdHecho(solicitud.getHecho().getId());
      dto.setTituloHecho(solicitud.getHecho().getTitulo());
    }

    return dto;
  }
}