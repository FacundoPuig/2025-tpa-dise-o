package ar.utn.ba.dsi.servicioAgregador.services.impl;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.UserRolesPermissionsDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.SolicitudEliminacionAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.Coleccion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.fuentes.Fuente;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Categoria;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Etiqueta;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Origen;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Origenes;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Ubicacion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.Estados;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.SolicitudEliminacion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios.Visualizador;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IColeccionRepository;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IFuenteRepository;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IHechoAgregacionRepository;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.ISolicitudEliminacionAgregacionRepository;
import ar.utn.ba.dsi.servicioAgregador.services.IHechoService;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServicioAgregacion {

  @Autowired
  @Lazy
  private IColeccionRepository coleccionRepository;
  @Autowired
  private IHechoAgregacionRepository hechoAgregacionRepository;
  @Autowired
  private ISolicitudEliminacionAgregacionRepository solicitudEliminacionAgregacionRepository;
  @Autowired
  private final WebClient.Builder webClientBuilder;
  @Autowired
  private IHechoService hechoService;
  @Autowired
  private NormalizacionService normalizacionService;
  @Autowired
  private IFuenteRepository fuenteRepository;

  @Value("${proxy.ruta}")
  private String rutaProxy;
  @Value("${dinamica.ruta}")
  private String rutaDinamica;
  @Value("${estatica.ruta}")
  private String rutaEstatica;

  private List<String> fuentesDeInfo;
  @Autowired
  private ColeccionService coleccionService;
  //private List<Hecho> hechosPendientes = new ArrayList<>();

  public ServicioAgregacion(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  @PostConstruct
  public void init() {
    // Inicialización segura después de la inyección de dependencias
    this.fuentesDeInfo = List.of(this.rutaDinamica, this.rutaEstatica);
  }

  public void accederASolicitudes() {
    WebClient webClient = webClientBuilder.baseUrl("http://localhost:8081").build();

    webClient.get()
        .uri("/solicitudes")
        .retrieve()
        .bodyToFlux(SolicitudEliminacionAgregadorOutputDTO.class)
        .map(this::convertirASolicitud)
        .collectList()
        .subscribe(nuevasSolicitudes -> {
          List<SolicitudEliminacion> existentes = solicitudEliminacionAgregacionRepository.findAll();
          List<SolicitudEliminacion> noRepetidas = filtrarSolicitudesNoRepetidas(nuevasSolicitudes, existentes);
          solicitudEliminacionAgregacionRepository.saveAll(noRepetidas);
        });
  }

  private List<SolicitudEliminacion> filtrarSolicitudesNoRepetidas(List<SolicitudEliminacion> nuevas, List<SolicitudEliminacion> existentes) {
    return nuevas.stream()
        .filter(nueva -> existentes.stream()
            .noneMatch(existente -> existente.getNroSolicitud().equals(nueva.getNroSolicitud())))
        .toList();
  }

 // @Scheduled(fixedRate = 300000) // Cada hora
  @Scheduled(fixedRate =  120000) // Cada 2 minutos (para pruebas)
  public void refrescarHechosPendientes() {
    System.out.print("\n🔄 [DEBUG] Iniciando sincronización de fuentes (" + LocalDateTime.now() + ")...\n");

    // 1. Traemos TODAS las fuentes de la BD (Estática, Dinámica, Proxies Cátedra, Proxies Externos)
    List<Fuente> fuentesConfiguradas = fuenteRepository.findAll()
        .stream()
        .filter(f -> {
          String n = f.getNombreFuente();
          return n != null && (n.equalsIgnoreCase("DINAMICA") || n.equalsIgnoreCase("ESTATICA"));
        })
        .toList();

    if (fuentesConfiguradas.isEmpty()) {
      System.out.println("⚠️ [DEBUG] No hay fuentes configuradas en la base de datos.");
      return;
    }

    List<Mono<List<Hecho>>> peticiones = new ArrayList<>();

    for (Fuente fuente : fuentesConfiguradas) {
      String nombre = fuente.getNombreFuente();
      String url = fuente.getUrl();             // La URL completa guardada en BD

      // Corrección de URL
      if (!url.contains("/hechos") && !url.contains("/desastres")) {
        url = url.endsWith("/") ? url + "hechos" : url + "/hechos";
      }

      final String urlFinal = url; // Variable final para el lambda
      System.out.println("➡️ [DEBUG] Consultando fuente: " + nombre + " | URL: " + urlFinal);

      Mono<List<Hecho>> call = webClientBuilder.build()
          .get()
          .uri(urlFinal)
          .retrieve()
          .bodyToFlux(HechoAgregadorOutputDTO.class)
          // LOG 1: Ver si llega ALGO del endpoint
          //.doOnNext(dto -> System.out.println("   👀 [DEBUG] " + nombre + " envió: " + dto.getTitulo()))
          .map(dto -> {
            try {
              return convertirADominio(dto, nombre);
            } catch (Exception ex) {
              //System.err.println("   ⚠️ [DEBUG] Error convirtiendo DTO de " + nombre + ": " + ex.getMessage());
              return null; // Se filtrará si es null
            }
          })
          .filter(Objects::nonNull) // Evitar nulos por errores de conversión
          .collectList()
          // LOG 2: Ver cuántos quedaron después de convertir
          .doOnSuccess(lista -> System.out.println("   ✅ [DEBUG] Fuente " + nombre + " procesada. Total items: " + lista.size()))
          .onErrorResume(e -> {
            //System.err.println("❌ [DEBUG] Error CRÍTICO conectando con [" + nombre + "]: " + e.getMessage());
            return Mono.just(Collections.emptyList());
          });

      System.out.println(call);
      peticiones.add(call);
      System.out.println("✅ Petición preparada para fuente: " + nombre);
    }

    //System.out.println("⏳ [DEBUG] Esperando respuestas de " + peticiones.size() + " fuentes...");

    // 2. Procesar flujo
    Flux.fromIterable(peticiones)
        .flatMap(mono -> mono)
        .flatMap(lista -> {
          System.out.println("   🔄 [DEBUG] Aplanando lista de " + lista.size() + " hechos...");
          return Flux.fromIterable(lista);
        })
        .flatMap(hecho -> {
          // LOG 3: Ver qué entra a normalizar
          // System.out.println("   normalization -> " + hecho.getTitulo());
          return normalizacionService.normalizar(hecho)
              .doOnError(e -> System.err.println("   ⚠️ [DEBUG] Falló normalización de: " + hecho.getTitulo()));
        })
        .collectList()
        .subscribe(hechosListos -> {
          //System.out.println("📊 [DEBUG] Resumen Final del Ciclo:");

          // 1. GUARDAR NUEVOS (Solo si hay)
          if (!hechosListos.isEmpty()) {
            //System.out.println("   💾 Guardando " + hechosListos.size() + " hechos en la Base de Datos...");
            try {
              hechoService.guardarHechosMasivos(hechosListos);
              //System.out.println("   ✅ Guardado masivo exitoso.");
            } catch (Exception e) {
              //System.err.println("   ❌ Error al guardar en BD: " + e.getMessage());
              e.printStackTrace();
            }
          } else {
            //System.out.println("   📭 [DEBUG] No hay hechos nuevos desde las fuentes externas.");
          }

          // 2. RECALCULAR COLECCIONES (¡SIEMPRE!)
          // Esto debe correr siempre porque aunque no haya hechos nuevos,
          // pudiste haber creado una colección nueva que necesita "chupar" hechos viejos.
          //System.out.println("   🔄 [DEBUG] Ejecutando recálculo de colecciones (Match de criterios)...");
          try {
            coleccionService.recalcularColecciones();
            System.out.println("   ✅ Recálculo finalizado.");
          } catch (Exception e) {
            //System.err.println("   ❌ Error al recalcular colecciones: " + e.getMessage());
          }

        }, error -> {
          //System.err.println("❌ [DEBUG] Error FATAL en el flujo reactivo global: " + error.getMessage());
          error.printStackTrace();
        });
  }


  //@Scheduled(cron = "0 0 3,19 * * *") // A las 3am y 19hs
  @Scheduled(fixedRate =  120000)
  @Transactional
  public void ejecutarAlgoritmoDeConsenso() {
    List<Coleccion> colecciones = coleccionRepository.findAll();

    colecciones.forEach(coleccion -> {
      if (coleccion.getAlgoritmoConsenso() != null) {
        coleccion.getAlgoritmoConsenso().cumpleConConcenso(coleccion.getColeccionHechos());
        coleccionRepository.save(coleccion);
      }
    });
    System.out.println("Consenso ejecutado en todas las colecciones.");
  }

  private Hecho convertirADominio(HechoAgregadorOutputDTO hechoOutputDTO, String nombreFuente) {
    Hecho hecho = new Hecho();

    // --- 1. Título y Descripción ---
    String tituloSeguro = (hechoOutputDTO.getTitulo() != null && !hechoOutputDTO.getTitulo().isBlank())
        ? hechoOutputDTO.getTitulo()
        : "Hecho sin título";
    hecho.setTitulo(tituloSeguro);

    String desc = (hechoOutputDTO.getDescripcion() != null) ? hechoOutputDTO.getDescripcion() : "Sin descripción";
    hecho.setDescripcion(desc);

    // --- 2. Categoría ---
    if (hechoOutputDTO.getCategoria() != null) {
      hecho.setCategoria(new Categoria(hechoOutputDTO.getCategoria()));
    } else {
      hecho.setCategoria(new Categoria("Sin Categoría"));
    }

    // --- 3. LÓGICA DE ORIGEN UNIFICADA (CORREGIDA) ---
    Origen tipoOrigen;
    String nombreOrigenFinal = nombreFuente; // Por defecto, usamos el nombre de la fuente (ej: "API Cátedra")

    if (nombreFuente.equalsIgnoreCase("DINAMICA")) {
      tipoOrigen = Origen.DINAMICA;
    } else if (nombreFuente.equalsIgnoreCase("ESTATICA")) {
      tipoOrigen = Origen.ESTATICA;
      // Si es Estática, preferimos el nombre del archivo CSV si viene en el DTO
      if (hechoOutputDTO.getNombreOrigen() != null && !hechoOutputDTO.getNombreOrigen().isBlank()) {
        nombreOrigenFinal = hechoOutputDTO.getNombreOrigen();
      }
    } else {
      tipoOrigen = Origen.PROXY;
    }

    hecho.setOrigen(new Origenes(nombreOrigenFinal, tipoOrigen));
    // -------------------------------------------------

    // --- 4. Ubicación ---
    if (!Double.isNaN(hechoOutputDTO.getLatitud()) && !Double.isNaN(hechoOutputDTO.getLongitud())) {
      Ubicacion u = new Ubicacion(hechoOutputDTO.getLatitud(), hechoOutputDTO.getLongitud());
      // Si el DTO ya trae la provincia normalizada, la aprovechamos
      if (hechoOutputDTO.getProvincia() != null) {
        u.setProvincia(hechoOutputDTO.getProvincia());
      }
      hecho.setUbicacion(u);
    } else {
      hecho.setUbicacion(new Ubicacion(0.0, 0.0));
    }

    // --- 5. Fechas y Multimedia ---
    hecho.setFechaAcontecimiento(hechoOutputDTO.getFechaAcontecimiento() != null
        ? hechoOutputDTO.getFechaAcontecimiento() : LocalDateTime.now());

    hecho.setFechaCarga(hechoOutputDTO.getFechaCarga() != null
        ? hechoOutputDTO.getFechaCarga() : LocalDate.now());

    hecho.setContenidoMultimedia(hechoOutputDTO.getContenidoMultimedia());

    // --- 6. Etiquetas ---
    List<Etiqueta> etiquetas = Optional.ofNullable(hechoOutputDTO.getNombreEtiquetas())
        .orElse(Collections.emptyList())
        .stream()
        .filter(e -> e != null && !e.isBlank())
        .map(Etiqueta::new)
        .collect(Collectors.toList());

    hecho.setEtiquetas(etiquetas);

    return hecho;
  }

  private SolicitudEliminacion convertirASolicitud(SolicitudEliminacionAgregadorOutputDTO dto) {
    SolicitudEliminacion solicitud = new SolicitudEliminacion();
    solicitud.setNroSolicitud(dto.getNroDeSolicitud());

    // Ojo aquí: podría devolver null si el hecho no existe todavía
    Hecho hecho = hechoAgregacionRepository.findByTitulo(dto.getNombreHecho());
    solicitud.setUnHecho(hecho);

    try {
      solicitud.setEstado(Estados.valueOf(dto.getEstado()));
    } catch (Exception e) {
      solicitud.setEstado(Estados.PENDIENTE);
    }

    solicitud.setMotivo(dto.getMotivo());
    solicitud.setFechaCreacionSolicitud(dto.getFechaCreacionSolicitud());

    return solicitud;
  }

  public Mono<List<HechoAgregadorOutputDTO>> recuperarHechosDeOtraInstancia(String instanciaURL) {

    if(instanciaURL == null || instanciaURL.isBlank())
      return Mono.just(Collections.emptyList());


    return webClientBuilder.baseUrl(rutaProxy).build()
        .get()
        .uri(uriBuilder -> uriBuilder.path("/hechos").queryParam("instanciaUrl", instanciaURL).build())
        .retrieve()
        .bodyToFlux(HechoAgregadorOutputDTO.class)
        .collectList()
        .onErrorResume(e -> {
          System.err.println("⚠️ Error recuperando hechos de Otra Instancia: " + e.getMessage());
          return Mono.just(Collections.emptyList());
        });
  }

  public Mono<List<HechoAgregadorOutputDTO>> recuperarHechosDeProxyCatedra() {
    return webClientBuilder.baseUrl(rutaProxy).build()
        .get()
        .uri("/desastres")
        .retrieve()
        .bodyToFlux(HechoAgregadorOutputDTO.class)
        .collectList()
        .onErrorResume(e -> {
          System.err.println("⚠️ Error recuperando hechos de Cátedra: " + e.getMessage());
          return Mono.just(Collections.emptyList());
        });
  }

  public UserRolesPermissionsDTO convertirAUserRolesPermissionsDTO(Visualizador visualizador) {
    if (visualizador == null || visualizador.getUsuario() == null || visualizador.getRolUsuario() == null) {
      return null;
    }
    String email = visualizador.getUsuario().getEmail();
    String nombreRol = visualizador.getRolUsuario().getNombrePermiso();
    List<String> permisosComoString = visualizador.getRolUsuario().getListaDePermisos()
        .stream()
        .map(Enum::name)
        .collect(Collectors.toList());

    return UserRolesPermissionsDTO.builder()
        .email(email)
        .nombreRol(nombreRol)
        .permisos(permisosComoString)
        .build();
  }
}