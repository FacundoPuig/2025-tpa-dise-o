package ar.utn.ba.dsi.servicioAgregador.controller;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.ColeccionEditarInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.ColeccionInputManual_DTO;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.PaginaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso.Algoritmos;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso.AlgoritmosFactory;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros.Filtros;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros.FiltrosFactory;
import ar.utn.ba.dsi.servicioAgregador.services.client.VisualizadorServiceClient;
import ar.utn.ba.dsi.servicioAgregador.services.impl.ColeccionService;
import ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios.Permiso;
import ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios.Visualizador;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IVisualizadorRepository;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/colecciones")
@CrossOrigin(origins = "${frontend.client.url}")
public class ColeccionController {
  @Autowired
  private ColeccionService coleccionService;
  @Autowired
  private IVisualizadorRepository visualizadorRepository;
  @Autowired
  private AlgoritmosFactory algoritmoFactory;
  @Autowired
  private FiltrosFactory filtrosFactory;
  @Autowired
  private VisualizadorServiceClient visualizadorServiceClient;

  public ColeccionController(ColeccionService coleccionService, AlgoritmosFactory algoritmoFactory, FiltrosFactory filtrosFactory) {
    this.coleccionService = coleccionService;
    this.algoritmoFactory = algoritmoFactory;
    this.filtrosFactory = filtrosFactory;
  }

  // API admi de MetaMapa
  @GetMapping("")
  public List<ColeccionOutputDTO> buscarTodas(){
    return coleccionService.buscarTodas();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ColeccionOutputDTO> buscarPorId(@PathVariable("id") String id,
                                                        @RequestParam(name = "urlInstance", defaultValue = "")  String urlInstance) {
    ColeccionOutputDTO coleccion = coleccionService.buscarPorId(id,urlInstance);
    if (coleccion == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(coleccion);
  }

  @PostMapping("")
  public ResponseEntity<ColeccionOutputDTO> guardarNuevaColeccion(@RequestBody ColeccionEditarInputDTO coleccion, // <-- CAMBIO DE FIRMA
                                                                  Authentication authentication) {
    // 1. OBTENER el email del usuario autenticado del token
    String emailUsuario = authentication.getName();

    Long idVisualizadorBody;
      idVisualizadorBody = Long.parseLong(coleccion.getVisualizadorID());
    try {
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("El ID del visualizador en el cuerpo es inválido.");
    }

    // 3. LLAMADA REMOTA al Servicio Usuarios para validar la identidad
    boolean esValido;
      esValido = visualizadorServiceClient.verificarIdDeUsuario(emailUsuario, idVisualizadorBody);
    System.out.println(esValido);
    try {
    } catch (RuntimeException e) {
      // Captura y relanza errores de comunicación (5xx)
      throw new RuntimeException("Fallo en la comunicación con el Servicio de Usuarios durante la verificación de identidad.", e);
    }


    if (!esValido) {
      // El cliente devolvió FALSE (capturó el 403 FORBIDDEN del servicio de Usuarios)
      throw new RuntimeException("No tiene permisos para crear una colección. El ID: " + idVisualizadorBody + " no corresponde al usuario autenticado.");
    }


    // Si pasa todas las validaciones
    ColeccionOutputDTO coleccionCreada = coleccionService.crear(coleccion);

    // Devolvemos el DTO de la nueva colección con estado 201 Created
    return ResponseEntity.status(HttpStatus.CREATED).body(coleccionCreada);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarColeccion(
      @PathVariable(name = "id") String id,
      @RequestParam(name = "visualizadorID") String visualizadorID,
      Authentication authentication){

    // Obtener el ID del usuario autenticado del token
    String emailUsuario = authentication.getName();
    Long idVisualizadorBody;

    try {
      idVisualizadorBody = Long.parseLong(visualizadorID);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("El ID del visualizador en el request param es inválido.");
    }

    // Paso 2: Coherencia de identidad (Llamada remota al servicio de usuarios)
    boolean esValido = visualizadorServiceClient.verificarIdDeUsuario(emailUsuario, idVisualizadorBody);

    if (!esValido) {
      throw new IllegalStateException("No tiene permisos para eliminar la colección. El ID no corresponde al usuario autenticado.");
    }
    if ("proxy-catedra-desastres".equals(id) || "proxy-otra-instancia".equals(id)) {
      // Retorna 403 o 400 ya que no se puede eliminar una colección proxy.
      throw new IllegalStateException("No se permite eliminar colecciones proxy.");
    }
    ColeccionOutputDTO coleccionBuscada = coleccionService.buscarPorId(id,null);
    if (coleccionBuscada == null) {
      throw new IllegalStateException("Colección no encontrada");
    }

    coleccionService.eliminar(id);
    return ResponseEntity.ok().build();
  }


//============= unificado (de una coleccion :fuentes, crit de pertenecia, algoritmo, agreagar y sacar hechos, titulo y descripcion)
@PutMapping("/{handleId}")
public ResponseEntity<ColeccionOutputDTO> editarColeccion(
    @PathVariable(name = "handleId") String handleId,
    @RequestBody ColeccionEditarInputDTO edicionDTO,
    Authentication authentication) {

  String emailUsuario = authentication.getName();
  Long idVisualizadorBody;

  try {
    idVisualizadorBody = Long.parseLong(edicionDTO.getVisualizadorID());
  } catch (NumberFormatException e) {
    throw new IllegalArgumentException("El ID del visualizador en el cuerpo es inválido.");
  }

  //Coherencia de identidad (Llamada remota al servicio de usuarios)
  boolean esValido = visualizadorServiceClient.verificarIdDeUsuario(emailUsuario, idVisualizadorBody);

  if (!esValido) {
    throw new IllegalStateException("No tiene permisos para editar la colección. El ID no corresponde al usuario autenticado.");
  }

  if ("proxy-catedra-desastres".equals(handleId) || "proxy-otra-instancia".equals(handleId)) {
    // Devolvemos un 403 o lanzamos una excepción clara.
    // Usaremos un IllegalStateException que puede mapearse a 400 Bad Request/403 Forbidden.
    throw new IllegalStateException("No se permite actualizar colecciones proxy.");
  }

  ColeccionOutputDTO coleccionBuscada = coleccionService.buscarPorId(handleId, null);
  if (coleccionBuscada == null) {
    throw new IllegalStateException("Colección no encontrada");
  }

  // Cambiar Titulo
  if (edicionDTO.getTitulo() != null) {
    coleccionService.cambiarTituloDeColeccion(handleId, edicionDTO.getTitulo());
  }

  // Cambiar Descripcion
  if (edicionDTO.getDescripcion() != null) {
    coleccionService.cambiarDescripcionDeColeccion(handleId, edicionDTO.getDescripcion());
  }

  // Cambiar Algoritmo de Consenso
  if (edicionDTO.getAlgoritmoConsenso() != null) {
    Algoritmos nuevoAlgoritmo = algoritmoFactory.crearInstancia(edicionDTO.getAlgoritmoConsenso());
    coleccionService.cambiarAlgoritmoConsenso(handleId, nuevoAlgoritmo);
  }

  // Modificar Fuentes (Reemplazo total)
  if (edicionDTO.getFuentes() != null) {
    coleccionService.modificarFuentes(handleId, edicionDTO.getFuentes());
  }

  // No se puede sacar o agregar hechos a mano, asi que esto no se usaria
  // Modificar Hechos de la coleccion (Reemplazo Total)
  /*if (edicionDTO.getIdDeHechosQueSeQuedanEnLaColeccion() != null && !edicionDTO.getIdDeHechosQueSeQuedanEnLaColeccion().isEmpty()) {
    coleccionService.modificarHechosDeColeccion(handleId, edicionDTO.getIdDeHechosQueSeQuedanEnLaColeccion());
  }*/
  /*if (edicionDTO.getIdDeHechosParaEliminar() != null && !edicionDTO.getIdDeHechosParaEliminar().isEmpty()) {
    coleccionService.eliminarHechosDeColeccion(handleId, edicionDTO.getIdDeHechosParaEliminar());
  }*/

  // Modificar Criterios de Pertenencia (Reemplazo Total)
  if (edicionDTO.getCriteriosPertenenciaNombres() != null && !edicionDTO.getCriteriosPertenenciaNombres().isEmpty()) {
    coleccionService.editarCriteriosDePertenencia(handleId, edicionDTO.getCriteriosPertenenciaNombres(), edicionDTO.getCriteriosPertenenciaValores());
  }

  ColeccionOutputDTO coleccionActualizada = coleccionService.buscarPorId(handleId, null);
  return new ResponseEntity<>(coleccionActualizada, HttpStatus.OK);
}



  // API publica para otras instancias (Con Paginación)
/*  @GetMapping("/{handleID}/hechos")
  public ResponseEntity<PaginaDTO<HechoAgregadorOutputDTO>> buscarHechosPorHandleId(
      @PathVariable("handleID") String handleID,
      @RequestParam(name = "instanciaUrl", defaultValue = "") String urlInstance,
      @RequestParam(name= "page" ,defaultValue = "0") int page,
      @RequestParam(name= "size", defaultValue = "10") int size
  )
  {

    System.out.println("Buscando hechos de la colección con handleID: " + handleID + ", página: " + page + ", tamaño: " + size);
    PaginaDTO<HechoAgregadorOutputDTO> pagina = coleccionService.devolverHechosDeColeccionPaginados(handleID, page, size, urlInstance);
    return ResponseEntity.ok(pagina);
  }*/

  // Modo de navegación curado o irrestricto
  @GetMapping("/{handleID}/hechos/navegacion") // ESTE LO VOY A USAR PARA MOSTRAR HECHOS DE LA COLECCION IRRESTRICTO (POR DEFECTO) Y CURADO
  public ResponseEntity<PaginaDTO<HechoAgregadorOutputDTO>> obtenerHechosSegunModo(
      @PathVariable("handleID") String handleID,
      @RequestParam(name = "esModoCurado", defaultValue = "false") Boolean esModoCurado,
      @RequestParam(name = "instanciaUrl", defaultValue = "") String urlInstance,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size
  ) {
    PaginaDTO<HechoAgregadorOutputDTO> pagina = coleccionService.devolverHechosSegunModoPaginado(esModoCurado, handleID, urlInstance, page, size);
    return ResponseEntity.ok(pagina);
  }


  // Navegación filtrada sobre una colección
  @GetMapping("/{handleID}/hechos/filtrar")
  public ResponseEntity<PaginaDTO<HechoAgregadorOutputDTO>> filtrarHechos(
      @PathVariable("handleID") String handleID,
      @RequestParam Map<String, String> parametrosDeFiltro,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size
  ) {
    parametrosDeFiltro.remove("page");
    parametrosDeFiltro.remove("size");

    List<Filtros> filtros = filtrosFactory.crearDesdeParametros(parametrosDeFiltro);
    return ResponseEntity.ok(coleccionService.filtrarHechosDeColeccion(handleID, filtros, page, size));
  }

  //Esto es para ESTADISTICAS
  @GetMapping("/hechosConHandleID")
  public List<HechoAgregadorOutputDTO> devolverHechosConHandleId(){
    return coleccionService.conseguirHechosDeColecciones();
  }

  @GetMapping("/metadata/consensus")
  public Map<String, String> getConsensusAlgorithms() {
    return Map.of(
        "MAYORIA_ABSOLUTA", "Mayoría Absoluta (Todas las fuentes)",
        "MAYORIA_SIMPLE", "Mayoría Simple (Más del 50% de las fuentes)",
        "MULTIPLES_MENCIONES", "Múltiples Menciones (Al menos 2 fuentes)"
    );
  }

  //devolver un hecho especifico de una coleccion
  @GetMapping("/{handleID}/hechos/{hechoID}/{instanciaUrl}")
  public HechoAgregadorOutputDTO devolverHechoEspecificoDeColeccion(
      @PathVariable("instanciaUrl") String instanciaUrl,
      @PathVariable("handleID") String handleID,
      @PathVariable("hechoID") String hechoID)
  {
    return coleccionService.devolverHechoEspecificoDeColeccion(handleID, hechoID, instanciaUrl);
  }
}