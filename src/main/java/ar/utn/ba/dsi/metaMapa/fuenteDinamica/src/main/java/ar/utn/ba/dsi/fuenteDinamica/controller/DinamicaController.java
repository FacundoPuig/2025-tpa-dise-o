package ar.utn.ba.dsi.fuenteDinamica.controller;

import ar.utn.ba.dsi.fuenteDinamica.models.dtos.ApiResponse;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.EdicionInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.HechoDinamicaInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.dtos.output.*;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Edicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoEdicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IEdicionRepository;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IHechoDinamicaRepository;
import ar.utn.ba.dsi.fuenteDinamica.services.IDinamicaService;
import ar.utn.ba.dsi.fuenteDinamica.services.IEdicionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dinamica")
@CrossOrigin(origins = { "${frontend.client.url}", "${agregador.ruta}" })
public class DinamicaController {

  @Autowired
  private IDinamicaService dinamicaService;
  @Autowired
  private IEdicionService edicionService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private IEdicionRepository edicionRepository;

  @PostMapping(value = "/hechos", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<ApiResponse<HechoDinamicaOutputDTO>> crearHecho(
      @RequestPart("hechoData") String hechoDataString,
      @RequestPart(value = "archivo", required = false) MultipartFile archivo,
      @AuthenticationPrincipal String userId) {
    try {
      HechoDinamicaInputDTO hechoInput = objectMapper.readValue(hechoDataString, HechoDinamicaInputDTO.class);
      hechoInput.setIdVisualizador(userId);
      HechoDinamicaOutputDTO hechoCreado = dinamicaService.crear(hechoInput, archivo, userId);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse<>(201, "OK", "Hecho creado exitosamente", hechoCreado));

    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error al leer el JSON del hecho", e);
    }
  }

  @GetMapping("/hechos")
  public List<HechoDinamicaOutputDTO> enviarHechos() {
    System.out.println("Recibiendo solicitud para listar todos los hechos dinámicos.");
    List<HechoDinamicaOutputDTO> lista = dinamicaService.enviarAAgregador();
    System.out.println(lista);
    return lista;
  }

  /*@GetMapping("/hechos/{id}")
  public ResponseEntity<ApiResponse<HechoDinamicaOutputDTO>> buscarHechoPorId(@PathVariable long id) {
    HechoDinamicaOutputDTO hecho = dinamicaService.buscarPorId(id);
    if (hecho == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(404, "Hecho no encontrado o eliminado."));
    }
    return ResponseEntity.ok(ApiResponse.success(hecho, "Hecho encontrado"));
  }*/

  @GetMapping("/hechos/{id}")
  public ResponseEntity<ApiResponse<HechoDinamicaOutputDTO>> obtenerHechoPorId(
      @PathVariable Long id) {

    HechoDinamicaOutputDTO hecho = dinamicaService.buscarPorId(id);

    return ResponseEntity.ok(ApiResponse.success(hecho, "Hecho encontrado"));
  }

  @PutMapping(value = "/hechos/{id}/editar", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<ApiResponse<Void>> proponerEdicion(
      @PathVariable long id,
      @RequestPart("edicionData") String edicionDataString,
      @RequestPart(value = "archivo", required = false) MultipartFile archivo,
      @AuthenticationPrincipal String userId) {
    try {
      EdicionInputDTO edicionInput = objectMapper.readValue(edicionDataString, EdicionInputDTO.class);
      edicionService.crearEdicion(id, edicionInput, userId, archivo);

      return ResponseEntity.ok(ApiResponse.success(null, "Edición propuesta creada exitosamente."));

    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error al leer el JSON", e);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error interno: " + e.getMessage());
    }
  }

  @GetMapping("/hechos/usuario/mis-hechos")
  public ResponseEntity<ApiResponse<List<HechoDinamicaOutputDTO>>> buscarPorUsuario(Authentication authentication) {
    String username = authentication.getName();

    List<HechoDinamicaOutputDTO> lista = dinamicaService.buscarHechosPorUsuario(username);
    return ResponseEntity.ok(ApiResponse.success(lista, "Hechos del usuario obtenidos"));
  }


  @GetMapping("/ediciones/{id}")
  public ResponseEntity<ApiResponse<EdicionConOriginalDTO>> verEdicion(@PathVariable long id, @AuthenticationPrincipal String revisorId) {
    Edicion ed = edicionRepository.findById(id).orElse(null);
    if (ed == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error(404, "Edición no encontrada"));
    }

    EdicionConOriginalDTO dto = new EdicionConOriginalDTO();
    dto.setId(ed.getId());
    dto.setTituloPropuesto(ed.getTituloPropuesto());
    dto.setDescripcionPropuesta(ed.getDescripcionPropuesta());
    dto.setCategoriaPropuestaId(ed.getCategoriaPropuesta().getId());
    dto.setCategoriaPropuestaNombre(ed.getCategoriaPropuesta().getNombre());
    dto.setLatitudPropuesta(ed.getLatitudPropuesta());
    dto.setLongitudPropuesta(ed.getLongitudPropuesta());
    dto.setFechaAcontecimientoPropuesta(ed.getFechaAcontecimientoPropuesta());
    dto.setContenidoMultimediaPropuesto(ed.getContenidoMultimediaPropuesto());
    dto.setVisualizadorEditor(ed.getVisualizadorEditorId());
    dto.setDetalle(ed.getDetalle());
    dto.setFechaEdicion(ed.getFechaEdicion().atStartOfDay()); // si tu campo es LocalDate
    dto.setEstado(ed.getEstado());

    // Setear hecho original
    Hecho hechoOriginal = ed.getIdHechoOriginal();
    if (hechoOriginal != null) {
      HechoOriginalDTO originalDTO = new HechoOriginalDTO();
      originalDTO.setId(hechoOriginal.getId());
      originalDTO.setTitulo(hechoOriginal.getTitulo());
      originalDTO.setDescripcion(hechoOriginal.getDescripcion());
      originalDTO.setCategoriaNombre(hechoOriginal.getCategoria().getNombre());
      dto.setHechoOriginal(originalDTO);
    }

    return ResponseEntity.ok(ApiResponse.success(dto, "Detalle de edición"));
  }



  /*@GetMapping("/ediciones/pendientes")
  public ResponseEntity<ApiResponse<List<EdicionOutputDTO>>> listarEdicionesPendientes(@AuthenticationPrincipal String revisorId) {
    List<EdicionOutputDTO> ediciones = edicionService.buscarListadeHechosPendientesEdicion(revisorId);
    return ResponseEntity.ok(ApiResponse.success(ediciones, "Ediciones pendientes recuperadas"));
  }*/

  @GetMapping("/ediciones/pendientes")
  public ResponseEntity<ApiResponse<List<EdicionConOriginalDTO>>> listarEdicionesPendientes() {
    List<Edicion> ediciones = edicionRepository.findByEstado(EstadoEdicion.PENDIENTE);

    List<EdicionConOriginalDTO> dtos = ediciones.stream().map(ed -> {
      EdicionConOriginalDTO dto = new EdicionConOriginalDTO();
      dto.setId(ed.getId());
      dto.setTituloPropuesto(ed.getTituloPropuesto());
      dto.setDescripcionPropuesta(ed.getDescripcionPropuesta());
      dto.setCategoriaPropuestaId(ed.getCategoriaPropuesta().getId());
      dto.setCategoriaPropuestaNombre(ed.getCategoriaPropuesta().getNombre());
      dto.setLatitudPropuesta(ed.getLatitudPropuesta());
      dto.setLongitudPropuesta(ed.getLongitudPropuesta());
      dto.setFechaAcontecimientoPropuesta(ed.getFechaAcontecimientoPropuesta());
      dto.setContenidoMultimediaPropuesto(ed.getContenidoMultimediaPropuesto());
      dto.setVisualizadorEditor(ed.getVisualizadorEditorId());
      dto.setDetalle(ed.getDetalle());
      dto.setFechaEdicion(ed.getFechaEdicion().atStartOfDay()); // si es LocalDate
      dto.setEstado(ed.getEstado());

      // Setear hecho original
      Hecho hechoOriginal = ed.getIdHechoOriginal();
      if (hechoOriginal != null) {
        HechoOriginalDTO originalDTO = new HechoOriginalDTO();
        originalDTO.setId(hechoOriginal.getId());
        originalDTO.setTitulo(hechoOriginal.getTitulo());
        originalDTO.setDescripcion(hechoOriginal.getDescripcion());
        originalDTO.setCategoriaNombre(hechoOriginal.getCategoria().getNombre());
        dto.setHechoOriginal(originalDTO);
      }

      return dto;
    }).toList();

    return ResponseEntity.ok(ApiResponse.success(dtos, "Ediciones pendientes recuperadas"));
  }

  @GetMapping("/ediciones/usuario/mis-ediciones")
  public ResponseEntity<ApiResponse<List<EdicionOutputDTO>>> listarEdicionesPorUsuario(Authentication authentication) {
    String userId = authentication.getName();
    return ResponseEntity.ok(ApiResponse.success(edicionService.buscarPorUsuario(userId), "Ediciones del usuario"));
  }

  @PutMapping("/ediciones/{id}/aceptar")
  public ResponseEntity<ApiResponse<Void>> aceptarEdicion(@PathVariable long id, @AuthenticationPrincipal String revisorId) {
    edicionService.aceptarEdicion(id, revisorId);
    return ResponseEntity.ok(ApiResponse.success(null, "Edición aceptada. Se generó una nueva versión."));
  }

  @PutMapping("/ediciones/{id}/rechazar")
  public ResponseEntity<ApiResponse<Void>> rechazarEdicion(@PathVariable long id, @AuthenticationPrincipal String revisorId) {
    edicionService.rechazarEdicion(id, revisorId);
    return ResponseEntity.ok(ApiResponse.success(null, "Edición rechazada."));
  }

  @GetMapping("/hechos/historial")
  public ResponseEntity<ApiResponse<List<HechoDinamicaOutputDTO>>> listarTodosLosHechosAdmin() {
    return ResponseEntity.ok(ApiResponse.success(dinamicaService.buscarTodas(), "Historial completo de hechos"));
  }

  @GetMapping("/ediciones/historial")
  public ResponseEntity<ApiResponse<List<EdicionOutputDTO>>> listarTodasLasEdicionesAdmin() {
    return ResponseEntity.ok(ApiResponse.success(edicionService.buscarTodas(), "Historial completo de ediciones"));
  }


  @GetMapping("/hechos/pendientes")
  public ResponseEntity<ApiResponse<List<HechoDinamicaOutputDTO>>> listarHechosPendientes(@AuthenticationPrincipal String revisorId) {
    return ResponseEntity.ok(ApiResponse.success(dinamicaService.buscarPendientesRevision(), "Hechos pendientes de revisión"));
  }

  @PutMapping("/hechos/{id}/aprobar")
  public ResponseEntity<ApiResponse<Void>> aprobarHecho(@PathVariable("id") long id, @AuthenticationPrincipal String revisorId) {
    dinamicaService.aprobarHecho(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Hecho aprobado y visible."));
  }

  @PutMapping("/hechos/{id}/aceptar-con-sugerencia")
  public ResponseEntity<ApiResponse<Void>> aceptarConSugerencia(
      @PathVariable Long id,
      @RequestBody Map<String, String> body,
      @AuthenticationPrincipal String adminId) {

    String sugerencia = body.get("sugerencia");
    dinamicaService.aceptarConSugerencia(id, sugerencia, adminId);

    return ResponseEntity.ok(ApiResponse.success(null, "Hecho aceptado con sugerencia"));
  }

  @PutMapping("/hechos/{id}/rechazar")
  public ResponseEntity<ApiResponse<Void>> rechazarHecho(@PathVariable long id, @AuthenticationPrincipal String revisorId) {
    dinamicaService.rechazarHecho(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Hecho rechazado."));
  }

  @PostMapping("/hechos/{id}/etiqueta")
  public ResponseEntity<ApiResponse<Void>> asignarEtiqueta(@PathVariable long id, @RequestBody String nombreEtiqueta) {
    dinamicaService.asignarEtiqueta(id, nombreEtiqueta);
    return ResponseEntity.ok(ApiResponse.success(null, "Etiqueta asignada."));
  }

  @PutMapping("/hechos/{titulo}/ocultar")
  public ResponseEntity<ApiResponse<Void>> ocultarHecho(@PathVariable("titulo") String titulo) {
    dinamicaService.ocultarHecho(titulo);
    return ResponseEntity.ok(ApiResponse.success(null, "Hecho ocultado."));
  }
  // --- SOLICITUDES DE BAJA ---

  @PostMapping("/hechos/{id}/solicitar-eliminacion")
  public ResponseEntity<ApiResponse<Void>> solicitarBaja(@PathVariable long id, @RequestBody SolicitudInputDTO input, @AuthenticationPrincipal String userId) {
    dinamicaService.crearSolicitudEliminacion(id, input.getMotivo(), userId);
    return ResponseEntity.ok(ApiResponse.success(null, "Solicitud de eliminacion creada."));
  }

  @GetMapping("/solicitudes-eliminacion")
  public ResponseEntity<ApiResponse<List<SolicitudOutputDTO>>> listarSolicitudesBaja() {
    return ResponseEntity.ok(ApiResponse.success(dinamicaService.listarSolicitudesEliminacionPendientes(), "Solicitudes pendientes"));
  }

  @PutMapping("/solicitudes-eliminacion/{id}/resolver")
  public ResponseEntity<ApiResponse<Void>> resolverSolicitudBaja(@PathVariable long id, @RequestParam boolean aceptar) {
    dinamicaService.resolverSolicitudEliminacion(id, aceptar);
    String estado = aceptar ? "APROBADA" : "RECHAZADA";
    return ResponseEntity.ok(ApiResponse.success(null, "Solicitud de eliminacion " + estado));
  }
}