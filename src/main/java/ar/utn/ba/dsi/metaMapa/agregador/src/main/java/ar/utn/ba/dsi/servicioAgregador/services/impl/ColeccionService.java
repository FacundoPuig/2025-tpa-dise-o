package ar.utn.ba.dsi.servicioAgregador.services.impl;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.ColeccionEditarInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.ColeccionInputManual_DTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.HechoAgregadorInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.PaginaDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.Coleccion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso.Algoritmos;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso.AlgoritmosFactory;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros.Filtros;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros.FiltrosFactory;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.fuentes.Fuente;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Categoria;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Etiqueta;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Origen;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Origenes;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Ubicacion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.intermedia.ColeccionHecho;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IFuenteRepository;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IHechoAgregacionRepository;
import ar.utn.ba.dsi.servicioAgregador.services.IColeccionService;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IColeccionRepository;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.services.IHechoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ColeccionService implements IColeccionService {

  @Autowired
  private IColeccionRepository coleccionRepository;
  @Autowired
  private IHechoService hechoService;
  @Autowired
  @Lazy
  private ServicioAgregacion servicioAgregacion;
  @Autowired
  private final AlgoritmosFactory algoritmoFactory;
  @Autowired
  private IHechoAgregacionRepository hechoAgregacionRepository;
  @Autowired
  private FiltrosFactory filtrosFactory;
  @Autowired
  private IFuenteRepository fuenteRepository;


  @Value("${estatica.ruta}")
  private String rutaEstatica;
  @Value("${dinamica.ruta}")
  private String rutaDinamica;
  @Value("${proxy.ruta}")
  private String rutaProxy;

  private static final String PROXY_CATEDRA_ID = "proxy-catedra-desastres";
  private static final String PROXY_OTRA_INSTANCIA_ID = "proxy-otra-instancia";

  public ColeccionService(AlgoritmosFactory algoritmoFactory) {
    this.algoritmoFactory = algoritmoFactory;
  }

  @Override
  public List<ColeccionOutputDTO> buscarTodas() {
    List<Coleccion> coleccionesDB = this.coleccionRepository.findAll();

    List<ColeccionOutputDTO> dtos = coleccionesDB.stream()
        .map(this::coleccionOutputDTO)
        .collect(Collectors.toList());
    dtos.add(this.coleccionOutputDTO(crearColeccionProxyCatedra()));
   dtos.add(this.coleccionOutputDTO(crearColeccionProxyOtraInstancia(null)));

    return dtos;
  }

  private Coleccion crearColeccionProxyCatedra() {
    Coleccion coleccionProxy = new Coleccion();
    coleccionProxy.setHandleID(PROXY_CATEDRA_ID);
    coleccionProxy.setTitulo("Hechos de Desastres (Cátedra)");
    coleccionProxy.setDescripcion("Colección de hechos obtenidos directamente desde la fuente de la Cátedra.");
    coleccionProxy.agregarFuente(new Fuente("PROXY", rutaProxy + "/desastres"));

    List<HechoAgregadorOutputDTO> hechosDTO = servicioAgregacion.recuperarHechosDeProxyCatedra().block();
    if (hechosDTO != null) {
      List<Hecho> hechos = hechosDTO.stream()
          .map(this::convertirDtoAHecho)
          .collect(Collectors.toList());

      coleccionProxy.agregarHecho(hechos);
    }

    return coleccionProxy;
  }

  private Coleccion crearColeccionProxyOtraInstancia(String instanciaUrl) {
    Coleccion coleccionProxy = new Coleccion();
    coleccionProxy.setHandleID(PROXY_OTRA_INSTANCIA_ID);
    coleccionProxy.setTitulo("Hechos de Otra Instancia de MetaMapa");
    coleccionProxy.setDescripcion("Colección de hechos obtenidos de otra instancia de MetaMapa.");
    coleccionProxy.agregarFuente(new Fuente("PROXY", rutaProxy));

    List<HechoAgregadorOutputDTO> hechosDTO = servicioAgregacion.recuperarHechosDeOtraInstancia(instanciaUrl).block();

    if (hechosDTO != null) {
      List<Hecho> hechos = hechosDTO.stream()
          .map(this::convertirDtoAHecho)
          .collect(Collectors.toList());

      coleccionProxy.agregarHecho(hechos);
    }

    return coleccionProxy;
  }

  @Override // crea la coleccion pero no le pone los hechos, esta vacia
  public ColeccionOutputDTO crear(ColeccionEditarInputDTO coleccionInputManual_dto){
    var coleccion = new Coleccion();
    coleccion.setTitulo(coleccionInputManual_dto.getTitulo());
    coleccion.setDescripcion(coleccionInputManual_dto.getDescripcion());
    coleccion.setAlgoritmoConsenso(algoritmoFactory.crearInstancia(coleccionInputManual_dto.getAlgoritmoConsenso()));
    coleccion.setHandleID(UUID.randomUUID().toString());

    if (coleccionInputManual_dto.getFuentes() != null && !coleccionInputManual_dto.getFuentes().isEmpty()) {

      Set<Fuente> fuentesIniciales = coleccionInputManual_dto.getFuentes().stream()
          .map(this::buscarOCrearFuente)
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());

      coleccion.setFuentes(fuentesIniciales);
    }

    List<String> nombres = coleccionInputManual_dto.getCriteriosPertenenciaNombres();
    List<String> valores = coleccionInputManual_dto.getCriteriosPertenenciaValores();

    if (nombres != null && valores != null) {
      if (nombres.size() != valores.size()) {
        throw new IllegalArgumentException("Las listas de criterios deben tener el mismo tamaño.");
      }

      List<Filtros> criteriosConvertidos = crearListaDeFiltros(nombres, valores);
      coleccion.setCriteriosPertenencia(criteriosConvertidos);
    }

    this.coleccionRepository.save(coleccion);
    return this.coleccionOutputDTO(coleccion);
  }

  public Fuente buscarOCrearFuente(String nombre) {
    // 1. Intentar buscar la Fuente existente
    Optional<Fuente> fuenteExistente = fuenteRepository.findByNombreFuenteIgnoreCase(nombre);

    if (fuenteExistente.isPresent()) {
      return fuenteExistente.get(); // Ya existe, la retornamos
    }

    // 2. Si no existe, crear la nueva instancia
    String url = switch (nombre.toUpperCase()) {
      case "DINAMICA" -> rutaDinamica;
      case "ESTATICA" -> rutaEstatica;
      default -> throw new IllegalArgumentException("Nombre de fuente no reconocido: " + nombre);
    };

    Fuente nuevaFuente = new Fuente(nombre.toUpperCase(), url);

    // 3. Guardarla en la base de datos y retornarla
    return fuenteRepository.save(nuevaFuente);
  }

  @Transactional
  public void recalcularColecciones() {
    List<Coleccion> colecciones = coleccionRepository.findAll();

    System.out.println("Iniciando recálculo de colecciones. Total a procesar: " + colecciones.size());

    if (colecciones.isEmpty()) {
      return;
    }
    for (Coleccion coleccion : colecciones) {
      Set<Origen> origenesAceptados = coleccion.getFuentes().stream()
          .map(Fuente::getNombreFuente)
          .map(nombre -> {
            String nombreUpper = nombre != null ? nombre.toUpperCase().trim() : "";

            // ⭐ Reemplazar el try/catch por un mapeo explícito
            return switch (nombreUpper) {
              case "DINAMICA" -> Origen.DINAMICA;
              case "ESTATICA" -> Origen.ESTATICA;
              case "PROXY" -> Origen.PROXY; // Si manejas fuentes proxy
              default -> null; // Si no es ninguna de las anteriores, se ignora
            };
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());

      if (origenesAceptados.isEmpty()) {
        System.out.println("❌ La colección no tiene fuentes válidas.");
      } else {
        // 2. Buscamos los candidatos según las fuentes marcadas
        List<Hecho> hechosCandidatos = hechoAgregacionRepository.findAllByOrigen_ProvieneDeInAndVisibleTrue(origenesAceptados);
        List<Hecho> hechosAunnNoEnColeccion = hechosCandidatos.stream()
            .filter(hecho -> !coleccion.getHechos().contains(hecho)).toList();

        // ⭐ DEBUG 1: Muestra los candidatos que llegaron
        System.out.println("--- DEBUG CANDIDATOS COLECCION: " + coleccion.getTitulo() + " ---");
        for (Hecho candidato : hechosCandidatos) {
          if (candidato.getTitulo().equals("obra teatral")) {
            System.out.println("Candidato 'obra teatral' encontrado. Origen: " + candidato.getOrigen().getNombre() + " | ID: " + candidato.getId());
          }
        }

        // 3. Aplicamos filtros (Categoría, Fecha, etc.) y agregamos
        // La exclusión del duplicado (Estática) ocurre DENTRO de agregarHechos,
        // porque el objeto Hecho de Estática es considerado .equals() al de Dinámica.
        coleccion.agregarHechos(hechosAunnNoEnColeccion);

        // ⭐ DEBUG 2: Muestra el contenido final de la colección
        System.out.println("--- DEBUG CONTENIDO FINAL COLECCION: " + coleccion.getTitulo() + " ---");
        for (Hecho hechoEnColeccion : coleccion.getHechos()) {
          if (hechoEnColeccion.getTitulo().equals("obra teatral")) {
            System.out.println("Hecho 'obra teatral' FINAL. Origen: " + hechoEnColeccion.getOrigen().getNombre());
          }
        }
      }

      coleccionRepository.save(coleccion);
    }
  }

  private List<Filtros> crearListaDeFiltros(List<String> nombres, List<String> valores) {
    if (nombres.size() != valores.size()) {
      throw new IllegalArgumentException("El número de nombres de criterios debe coincidir con el número de valores.");
    }

    List<Filtros> criteriosConvertidos = new ArrayList<>();
    int size = nombres.size();

    for (int i = 0; i < size; i++) {
      String nombreCriterio = nombres.get(i);
      String valorCriterio = valores.get(i);

      Filtros nuevoFiltro = filtrosFactory.crearInstancia(nombreCriterio, valorCriterio);

      criteriosConvertidos.add(nuevoFiltro);
    }
    return criteriosConvertidos;
  }

  // elimine los hechos de las colecciones cunado se ocultan
  public void eliminarHechoDeColecciones(Hecho hechoOculto) {

    System.out.println("Eliminando hecho oculto de colecciones... Hecho: " + hechoOculto);

    List<Coleccion> coleccionesConHecho = coleccionRepository.findAll().stream()
        .filter(coleccion -> coleccion.getHechos().contains(hechoOculto))
        .toList();

    System.out.println("Colecciones que contienen el hecho oculto:");
    System.out.println(coleccionesConHecho);
    System.out.println("Cantidad de colecciones afectadas: " + coleccionesConHecho.size());

    for (Coleccion coleccion : coleccionesConHecho) {
      ColeccionHecho ch = coleccion.getColeccionHechos().stream()
          .filter(relacion -> relacion.getHecho().equals(hechoOculto))
          .findFirst()
          .orElse(null);
      coleccion.getColeccionHechos().remove(ch);
      coleccionRepository.save(coleccion);
    }
  }

  public void update(String handleID, ColeccionInputManual_DTO coleccionModificada) {
    Coleccion coleccion = this.coleccionRepository.findById(handleID)
        .orElseThrow(()-> new ResourceNotFoundException("Coleccion con ID: " + handleID + " no encontrada.")); // si es de proxy no la encuentra en la bd
    coleccion.setTitulo(coleccionModificada.getTitulo());
    coleccion.setDescripcion(coleccionModificada.getDescripcion());
    coleccion.setAlgoritmoConsenso(algoritmoFactory.crearInstancia(coleccionModificada.getAlgoritmoConsenso()));
    this.coleccionRepository.save(coleccion);
  }

  /*public void modificarHechosDeColeccion(String handleID, List<Long> hechosQueQuedan) {
    Coleccion coleccion = this.coleccionRepository.findById(handleID)
        .orElseThrow(() -> new ResourceNotFoundException("Coleccion con ID: " + handleID + " no encontrada."));

    Set<Hecho> hechosACargar = hechosQueQuedan.stream()
        .map(id -> this.hechoAgregacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Hecho con ID: " + id + " no encontrado.")))
        .collect(Collectors.toSet());

    //elimino los hechos q tenia en la coleccion
    coleccion.getHechos().clear();

    //le cargo los de hechosACargar
    coleccion.getHechos().addAll(hechosACargar);
    this.coleccionRepository.save(coleccion);
  }*/

  public void eliminarHechosDeColeccion(String handleID, List<Long> idsParaEliminar) {
    Coleccion coleccion = this.coleccionRepository.findById(handleID)
        .orElseThrow(() -> new ResourceNotFoundException("Coleccion no encontrada"));

    // Removemos los hechos que coincidan con la lista de IDs
    boolean modificado = coleccion.getHechos().removeIf(h -> idsParaEliminar.contains(h.getId()));

    if (modificado) {
      this.coleccionRepository.save(coleccion);
    }
  }


/*  public ColeccionOutputDTO eliminarHechosDeColeccion(String handleID, List<HechoAgregadorOutputDTO> hechosAEliminarDTO) {

    Coleccion coleccion = this.coleccionRepository.findById(handleID)
        .orElseThrow(() -> new ResourceNotFoundException("Coleccion con ID: " + handleID + " no encontrada."));

    Set<Long> idsAEliminar = hechosAEliminarDTO.stream()
        .map(HechoAgregadorOutputDTO::getId)
        .collect(Collectors.toSet());

    boolean seModifico = coleccion
        .getHechos()
        .removeIf(hecho -> idsAEliminar.contains(hecho.getId())
    );

    if (seModifico) {
      this.coleccionRepository.save(coleccion);
    }

    return coleccionOutputDTO(coleccion);
  }*/

  @Override
  public void eliminar(String id) {
    // proxy no porque el administrador solo puede borrar colecciones de la instancia actual
    this.coleccionRepository.findById(id).ifPresent(coleccion -> this.coleccionRepository.delete(coleccion));
  }

  public void cambiarAlgoritmoConsenso(String id, Algoritmos nuevoAlgoritmo) {
    Coleccion coleccion = this.coleccionRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Coleccion con ID: " + id + " no encontrada."));
    coleccion.setAlgoritmoConsenso(nuevoAlgoritmo);
    this.coleccionRepository.save(coleccion);
  }

  public void modificarFuentes(String id, List<String> fuentesNombres) {
    Coleccion coleccion = this.coleccionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Coleccion con ID: " + id + " no encontrada."));

    if (coleccion.getFuentes() != null) {
      coleccion.getFuentes().clear();
    } else {
      coleccion.setFuentes(new HashSet<>());
    }

    if (fuentesNombres != null) {
      for (String nombre : fuentesNombres) {
        try {
          Fuente fuenteEntity = this.buscarOCrearFuente(nombre);
          coleccion.agregarFuente(fuenteEntity);
        } catch (IllegalArgumentException e) {
          System.err.println("Fuente desconocida ignorada: " + nombre);
        }
      }
    }

    this.coleccionRepository.save(coleccion);
  }

  public void editarCriteriosDePertenencia(String handleID, List<String> nuevosCriterios, List<String> valoresCriterios) {

    Coleccion coleccion = this.coleccionRepository.findById(handleID)
        .orElseThrow(() -> new ResourceNotFoundException("Colección con ID " + handleID + " no encontrada."));

    if (nuevosCriterios.size() != valoresCriterios.size()) {
      throw new IllegalArgumentException("Los nombres y valores de los criterios de pertenencia deben coincidir en tamaño.");
    }

    int size = nuevosCriterios.size();
    List<Filtros> criteriosConvertidos = new ArrayList<>();

    // Iteramos para crear las instancias de Filtros usando la Factory
    for (int i = 0; i < size; i++) {
      String nombreCriterio = nuevosCriterios.get(i);
      String valorCriterio = valoresCriterios.get(i);

      criteriosConvertidos.add(filtrosFactory.crearInstancia(nombreCriterio, valorCriterio));
    }

    List<Filtros> criteriosActuales = coleccion.getCriteriosPertenencia();
    criteriosActuales.clear();
    criteriosActuales.addAll(criteriosConvertidos); //Le aplica los criterios nuevos pero deberia modificar tmb la lista de hechos de esa coleccion

    this.coleccionRepository.save(coleccion);
  }

  public void cambiarTituloDeColeccion(String handleId, String tituloNuevo){
    Coleccion coleccion = coleccionRepository.findById(handleId).orElseThrow(() -> new RuntimeException("Colección con handleId " + handleId + " no encontrada."));
    coleccion.setTitulo(tituloNuevo);
    coleccionRepository.save(coleccion);
  }

  public void cambiarDescripcionDeColeccion(String handleId, String descripcionNueva){
    Coleccion coleccion = coleccionRepository.findById(handleId).orElseThrow(() -> new RuntimeException("Colección con handleId " + handleId + " no encontrada."));
    coleccion.setDescripcion(descripcionNueva);
    coleccionRepository.save(coleccion);
  }

  /*public void quitarFuentes(String id, List<String> fuentes) {
    Coleccion coleccion = this.coleccionRepository.findById(id)
        .orElseThrow(()-> new ResourceNotFoundException("Coleccion con ID: " + id + " no encontrada."));

    fuentes.forEach(fuente -> {
      switch (fuente)
      {
        case "DINAMICA":{
          Fuente nuevaDinamica = new Fuente("DINAMICA", rutaDinamica);
          coleccion.quitarFuente(nuevaDinamica);

          break;
        }
        case "ESTATICA":{
          Fuente nuevaEstatica = new Fuente("ESTATICA", rutaEstatica);
          coleccion.quitarFuente(nuevaEstatica);
          break;
        }
      }
    });

    this.coleccionRepository.save(coleccion);
  }*/

  
  public PaginaDTO<HechoAgregadorOutputDTO> devolverHechosDeColeccionPaginados(String id, int page, int size, String instanciaUrl) {

    List<HechoAgregadorOutputDTO> listaCompleta;
    Pageable pageable = PageRequest.of(page, size);

    // CASO 1: PROXIES (Paginación en Memoria)
    if (PROXY_CATEDRA_ID.equals(id)) {
      listaCompleta = servicioAgregacion.recuperarHechosDeProxyCatedra().block();
      return paginarEnMemoria(listaCompleta, pageable);
    }
    else if (PROXY_OTRA_INSTANCIA_ID.equals(id)) {
      listaCompleta = servicioAgregacion.recuperarHechosDeOtraInstancia(instanciaUrl).block();
      return paginarEnMemoria(listaCompleta, pageable);
    }

    // CASO 2: BASE DE DATOS LOCAL (Paginación Eficiente)
    else {
      // Usamos la query del repositorio que trae solo la página necesaria
      Page<Hecho> paginaHechos = hechoAgregacionRepository.findAllByColeccionId(id, pageable);

      // Convertimos Entidad -> DTO
      List<HechoAgregadorOutputDTO> contenidoDTO = paginaHechos.getContent().stream()
          .map(hecho -> hechoService.hechoAgregadorOutputDTO(hecho))
          .collect(Collectors.toList());

      return new PaginaDTO<>(
          contenidoDTO,
          paginaHechos.getNumber(),
          paginaHechos.getSize(),
          paginaHechos.getTotalElements(),
          paginaHechos.getTotalPages(),
          paginaHechos.isLast()
      );
    }
  }

  public PaginaDTO<HechoAgregadorOutputDTO> devolverHechosSegunModoPaginado(Boolean esModoCurado, String handleID, String urlInstance, int page, int size) {

    if (!esModoCurado){
      return this.devolverHechosDeColeccionPaginados(handleID, page, size, urlInstance); //es para el caso de irrestricto
    } else { // esto para el caso de q sea curado
      // 1. Llamamos a tu lógica vieja que obtiene la lista completa
      List<HechoAgregadorOutputDTO> listaCompleta = devolverHechosSegunModo(esModoCurado, handleID, urlInstance);

      // 2. Hacemos la paginación manual en memoria
      int totalElements = listaCompleta.size();
      int fromIndex = page * size;
      int toIndex = Math.min(fromIndex + size, totalElements);

      List<HechoAgregadorOutputDTO> contenidoPagina;

      if (fromIndex >= totalElements) {
        contenidoPagina = new java.util.ArrayList<>();
      } else {
        contenidoPagina = listaCompleta.subList(fromIndex, toIndex);
      }

      // 3. Devolvemos el DTO de Página que espera el Front
      return new PaginaDTO<>(
          contenidoPagina,
          page,
          size,
          (long) totalElements,
          (int) Math.ceil((double) totalElements / size),
          toIndex >= totalElements
      );
    }
  }

  public List<HechoAgregadorOutputDTO> devolverHechosSegunModo(Boolean esModoCurado, String handleID, String instanciaUrl) {

    // CASO 1: PROXIES (Paginación en Memoria)
    if (PROXY_CATEDRA_ID.equals(handleID)) {
      return servicioAgregacion.recuperarHechosDeProxyCatedra().block();

    }
    else if (PROXY_OTRA_INSTANCIA_ID.equals(handleID)) {
      return servicioAgregacion.recuperarHechosDeOtraInstancia(instanciaUrl).block();
    }
    else {
      Coleccion coleccion = coleccionRepository.findById(handleID)
          .orElseThrow(() -> new ResourceNotFoundException("Colección con ID " + handleID + " no encontrada."));

      List <ColeccionHecho> listaCompleta = coleccion.getColeccionHechos();
      List<Hecho> aux = Boolean.TRUE.equals(esModoCurado)
          ? listaCompleta.stream()
          .filter(ColeccionHecho::isEsConsensuado)
          .map(ColeccionHecho::getHecho)
          .toList()
          : coleccion.getHechos();

      return aux.stream().map(hechoService::hechoAgregadorOutputDTO).collect(Collectors.toList());
    }
  }


  public PaginaDTO<HechoAgregadorOutputDTO> filtrarHechosDeColeccion(String handleID, List<Filtros> filtros, int page, int size) {
    Coleccion coleccion = coleccionRepository.findById(handleID)
        .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada: " + handleID));

    // 1. Obtenemos TODOS (o los filtrados)
    List<Hecho> hechosFiltrados;
    if (filtros == null || filtros.isEmpty()) {
      hechosFiltrados = coleccion.getHechos();
    } else {
      hechosFiltrados = coleccion.aplicarFiltros(filtros).stream()
          .map(ColeccionHecho::getHecho)
          .toList();
    }

    // 2. Aplicamos paginación en memoria (sublista)
    int totalElements = hechosFiltrados.size();
    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, totalElements);

    List<HechoAgregadorOutputDTO> contenidoPagina;

    if (fromIndex >= totalElements) {
      contenidoPagina = new ArrayList<>();
    } else {
      List<Hecho> sublista = hechosFiltrados.subList(fromIndex, toIndex);
      contenidoPagina = sublista.stream()
          .map(hechoService::hechoAgregadorOutputDTO)
          .toList();
    }

    // 3. Construimos el DTO de Página
    // PaginaDTO(contenido, paginaActual, tamanioPagina, totalElementos, totalPaginas, esUltima)
    return new PaginaDTO<>(
        contenidoPagina,
        page,
        size,
        (long) totalElements,
        (int) Math.ceil((double) totalElements / size),
        toIndex >= totalElements
    );
  }


  @Override
  public ColeccionOutputDTO buscarPorId(String id, String instanciaUrl) {
    if (PROXY_CATEDRA_ID.equals(id)) {
      return this.coleccionOutputDTO(crearColeccionProxyCatedra());
    }
    if (PROXY_OTRA_INSTANCIA_ID.equals(id)) {
      return this.coleccionOutputDTO(crearColeccionProxyOtraInstancia(instanciaUrl));
    }

    Coleccion coleccion = this.coleccionRepository.findById(id).orElse(null);
    if (coleccion == null) {
      return null;
    }
    return this.coleccionOutputDTO(coleccion);
  }

  public List<HechoAgregadorOutputDTO> devolverHechosDeColeccion(String id,String instanciaUrl) {
    if (PROXY_CATEDRA_ID.equals(id)) {
      return servicioAgregacion.recuperarHechosDeProxyCatedra().block();  // .block() convierte el resultado asíncrono (Mono) en síncrono

    } else if (PROXY_OTRA_INSTANCIA_ID.equals(id)) {
      return servicioAgregacion.recuperarHechosDeOtraInstancia(instanciaUrl).block();

    } else {
      var coleccion = this.coleccionRepository.findById(id).orElse(null);
      if (coleccion == null) {
        return null;
      }
      return coleccion.getHechos().stream()
          .map(hecho -> hechoService.hechoAgregadorOutputDTO(hecho))
          .collect(Collectors.toList());
    }
  }

  //devolver un hecho especifico de una coleccion
  public HechoAgregadorOutputDTO devolverHechoEspecificoDeColeccion (String handleID, String hechoID, String instanciaUrl) {

    List<HechoAgregadorOutputDTO> hechosDeColeccion = this.devolverHechosDeColeccion(handleID,instanciaUrl);
    if (hechosDeColeccion == null) {
      throw new ResourceNotFoundException("Colección con ID " + handleID + " no encontrada.");
    }

    for (HechoAgregadorOutputDTO hechoDTO : hechosDeColeccion) {
      if (hechoDTO.getId().toString().equals(hechoID)) {
        return hechoDTO;
      }
    }

    throw new ResourceNotFoundException("Hecho con ID " + hechoID + " no encontrado en la colección " + handleID + ".");

  }


  // ======= Metodos Privados para operar aca ======

  private Hecho convertidorInputAHecho(HechoAgregadorInputDTO dto) {
    Hecho hecho = new Hecho();
    hecho.setTitulo(dto.getTitulo());
    hecho.setDescripcion(dto.getDescripcion());
    hecho.setCategoria(new Categoria(dto.getCategoria()));
    hecho.setFechaAcontecimiento(dto.getFechaAcontecimiento());
    hecho.setUbicacion(new Ubicacion(dto.getLatitud(),dto.getLongitud()));
    hecho.setContenidoMultimedia(dto.getContenidoMultimedia());
    hecho.setId(dto.getId());
    return hecho;
  }

  private Hecho convertirDtoAHecho(HechoAgregadorOutputDTO dto) {
    Hecho hecho = new Hecho();

    // Mapeos básicos con protección null-safe
    hecho.setTitulo(dto.getTitulo() != null ? dto.getTitulo() : "Sin título");
    hecho.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : "");
    hecho.setFechaAcontecimiento(dto.getFechaAcontecimiento());
    hecho.setFechaCarga(dto.getFechaCarga());

    // Ubicación segura
    double lat = Double.isNaN(dto.getLatitud()) ? 0.0 : dto.getLatitud();
    double lon = Double.isNaN(dto.getLongitud()) ? 0.0 : dto.getLongitud();
    hecho.setUbicacion(new Ubicacion(lat, lon));

    // Categoría segura
    String nombreCategoria = (dto.getCategoria() != null) ? dto.getCategoria() : "Sin Categoría";
    hecho.setCategoria(new Categoria(nombreCategoria));

    hecho.setContenidoMultimedia(dto.getContenidoMultimedia());

    // --- CORRECCIÓN DEL ERROR (NPE en Origen) ---
    String nombreOrigen = (dto.getNombreOrigen() != null) ? dto.getNombreOrigen() : "Desconocido";
    String origenStr = dto.getProvieneDeOrigen();

    // Valor por defecto si viene nulo
    Origen origenEnum = Origen.PROXY;

    if (origenStr != null) {
      try {
        origenEnum = Origen.valueOf(origenStr);
      } catch (IllegalArgumentException e) {
        System.err.println("Advertencia: Origen '" + origenStr + "' no válido. Se usará PROXY.");
      }
    }
    // Si origenStr era null, se queda con Origen.PROXY y no falla.

    hecho.setOrigen(new Origenes(nombreOrigen, origenEnum));
    // -------------------------------------------

    if (dto.getNombreEtiquetas() != null) {
      List<Etiqueta> etiquetas = dto.getNombreEtiquetas().stream()
          .map(Etiqueta::new)
          .collect(Collectors.toList());
      hecho.setEtiquetas(etiquetas);
    }

    return hecho;
  }

  private ColeccionOutputDTO coleccionOutputDTO(Coleccion coleccion) {
    ColeccionOutputDTO dto = new ColeccionOutputDTO();
    dto.setTitulo(coleccion.getTitulo());
    dto.setDescripcion(coleccion.getDescripcion());
    dto.setHandleID(coleccion.getHandleID());

    // 1. Algoritmo
    if (coleccion.getAlgoritmoConsenso() != null) {
      String clase = coleccion.getAlgoritmoConsenso().getClass().getSimpleName();
      // Mapeamos el nombre de la clase al Value del Select del Front
      switch (clase) {
        case "Absoluta": dto.setAlgoritmoConsenso("MAYORIA_ABSOLUTA"); break;
        case "MayoriaSimple": dto.setAlgoritmoConsenso("MAYORIA_SIMPLE"); break;
        case "MultiplesMenciones": dto.setAlgoritmoConsenso("MULTIPLES_MENCIONES"); break;
        default: dto.setAlgoritmoConsenso("");
      }
    } else {
      dto.setAlgoritmoConsenso("");
    }

    // 2. Fuentes (Nombres)
    if (coleccion.getFuentes() != null) {
      dto.setFuentes(coleccion.getFuentes().stream()
          .map(Fuente::getNombreFuente)
          .collect(Collectors.toList()));
    }

    // 3. Criterios (Resumen en String)
    if (coleccion.getCriteriosPertenencia() != null) {
      // Asumiendo que Filtros tiene un método toString o getDescripcion
      dto.setCriterios(coleccion.getCriteriosPertenencia().stream()
          .map(f -> f.getClass().getSimpleName() + ": " + f.getValorFiltro())
          .collect(Collectors.toList()));
    }

    return dto;
  }



  // Método auxiliar para cortar listas de Proxies (Paginación manual)
  private PaginaDTO<HechoAgregadorOutputDTO> paginarEnMemoria(List<HechoAgregadorOutputDTO> lista, Pageable pageable) {
    if (lista == null) lista = new ArrayList<>();

    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), lista.size());

    List<HechoAgregadorOutputDTO> sublista;
    if (start > lista.size()) {
      sublista = new ArrayList<>();
    } else {
      sublista = lista.subList(start, end);
    }

    return new PaginaDTO<>(
        sublista,
        pageable.getPageNumber(),
        pageable.getPageSize(),
        lista.size(),
        (int) Math.ceil((double) lista.size() / pageable.getPageSize()),
        end >= lista.size()
    );
  }

  // Desde el agregador debo mandar todos los hechos pero q sepan el handleId de la coleccion a la q pertenecen (PARA EVITAR EL REQUEST N+1)
  // lo hago aca xq tengo inyectado coleccion y hechos repository
  public List<HechoAgregadorOutputDTO> conseguirHechosDeColecciones() {

    List<Coleccion> colecciones = coleccionRepository.findAll();
    List<HechoAgregadorOutputDTO> todosLosHechos = new ArrayList<>();

    for (Coleccion coleccion : colecciones) {
      String handleId = coleccion.getHandleID();

      List<HechoAgregadorOutputDTO> hechosDeColeccion = this.devolverHechosDeColeccion(handleId,null);

      // aca seteo a cada hecho de esa coleccion con el handleId de la coleccion
      for (HechoAgregadorOutputDTO hechoDTO : hechosDeColeccion){

        hechoDTO.setHandleIdColeccion(handleId);
        todosLosHechos.add(hechoDTO);
      }
    }
    return todosLosHechos;
  }


}