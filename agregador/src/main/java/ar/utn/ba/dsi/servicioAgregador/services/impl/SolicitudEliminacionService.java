package ar.utn.ba.dsi.servicioAgregador.services.impl;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.SolicitudEliminacionAgregadorInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.RegistroEstadoOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.SolicitudEliminacionAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.RegistroCambioEstado;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IHechoAgregacionRepository;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IRegistroCambioEstadosRepository;
import ar.utn.ba.dsi.servicioAgregador.services.IColeccionService;
import ar.utn.ba.dsi.servicioAgregador.services.ISolicitudEliminacionService;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.ISolicitudEliminacionAgregacionRepository;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.IDetectorDeSpam;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.SolicitudEliminacion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.Estados;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitudEliminacionService implements ISolicitudEliminacionService {

  @Value("${dinamica.ruta}")
  private String urlDinamica;

  @Value("${estatica.ruta}")
  private String urlEstatica;

  @Value("${proxy.ruta}")
  private String urlProxy;

  @Autowired
  private ISolicitudEliminacionAgregacionRepository solicitudRepository;
  @Autowired
  private IHechoAgregacionRepository hechoRepository;
  @Autowired
  private IDetectorDeSpam detectorDeSpam;
  @Autowired
  private WebClient.Builder webClientBuilder;
  @Autowired
  private IRegistroCambioEstadosRepository registroCambioEstadosRepository;
  @Autowired
  private IColeccionService coleccionService;

  @Override
  public List<SolicitudEliminacionAgregadorOutputDTO> buscarTodas() {
    return this.solicitudRepository
        .findAll()
        .stream()
        .map(this::solicitudOutputDTO)
        .toList();
  }

  @Override
  public SolicitudEliminacionAgregadorOutputDTO buscarPorId(Integer id) {
    var solicitud = this.solicitudRepository.findById(id).orElse(null);
    if (solicitud == null) {
      return null;
    }
    return this.solicitudOutputDTO(solicitud);
  }


  @Override
  public void eliminar(Integer id) {
    /*var serie = this.solicitudRepository.findById(id);
    if (serie != null) {
      this.solicitudRepository.delete(serie);
    }*/
    this.solicitudRepository.deleteById(id);
  }

  @Override
  @Transactional
  public SolicitudEliminacionAgregadorOutputDTO crear(SolicitudEliminacionAgregadorInputDTO input, String userId) {
    var hecho = hechoRepository.findById(input.getId()).orElse(null);
    System.out.println("1. Entro a la creación de solicitud de eliminación");

    if (hecho == null) {
      throw new IllegalArgumentException("No existe un hecho con ese título.");
    }

    System.out.println("2. Hecho encontrado: " + hecho.getTitulo());

    SolicitudEliminacion solicitud = new SolicitudEliminacion(hecho, input.getMotivo(), userId);

    System.out.println(solicitud);

    if (!esValido(solicitud.getMotivo())) {
      throw new IllegalArgumentException("El motivo debe tener al menos 500 caracteres.");
    }

    System.out.println("3. Motivo válido, longitud: " + solicitud.getMotivo().length());

    Estados estadoInicial = Estados.PENDIENTE;
    String nota = "Solicitud creada y en espera de revisión.";

    //puedo crear solicitud si no existe otra aceptada
    if(solicitudRepository.findByEstado(Estados.ACEPTADO).stream()
        .anyMatch(s -> s.getUnHecho().getId() == hecho.getId()))
    {
      estadoInicial= Estados.RECHAZADO;
      nota = "Ya existe una solicitud aceptada para este hecho";
    }

    System.out.println("4. No existe solicitud aceptada previa para e ste hecho.");

    //detecto spam
    if (detectorDeSpam.esSpam(solicitud.getMotivo())) {
      estadoInicial = Estados.RECHAZADO;
      nota = "Sistema de detección de spam";
    }

    solicitud.setEstado(estadoInicial);

    // 5. FIX CRÍTICO: Guardamos PRIMERO la solicitud padre para generar su ID
    solicitud = solicitudRepository.save(solicitud);

    // 6. Ahora que tiene ID, agregamos el registro histórico
    generarYGuardarRegistro(solicitud, estadoInicial, userId, nota);

    // 7. Guardamos de nuevo para persistir la lista de registros (Cascade Update)
    solicitudRepository.save(solicitud);

    return this.solicitudOutputDTO(solicitud);
  }

  private void generarYGuardarRegistro(SolicitudEliminacion solicitud, Estados estado, String responsableId, String notas) {
    RegistroCambioEstado registro = new RegistroCambioEstado(estado, notas, solicitud);

    // Intentamos buscar el usuario (Visualizador) para dejar constancia
    if (responsableId != null) {
      try {
        // Si el ID es numérico (ej: ID de usuario en BD)
        long id = Long.parseLong(responsableId);
         //TODO ver como guardo al resdponsable del caMBIO CON EL AUTHENTICATION
      } catch (NumberFormatException e) {
        // Si no es numérico (es email o 'Sistema'), lo ignoramos o manejamos según necesidad
      }
    }
    // Agregamos a la lista de la entidad padre.
    solicitud.agregarEstado(registro);
    registroCambioEstadosRepository.save(registro);
  }

  private boolean esValido(String motivo) {
    return motivo != null && motivo.length() >= 500;
  }

  private void propagarBajaAFuente(Hecho hecho, Authentication authentication) {
    String urlFuente = "";

    System.out.println(authentication);

    // Determinamos a quién llamar según el origen
    switch (hecho.getOrigen().getProvieneDe()) {
      case DINAMICA:
        // Quitamos la barra final si viene doble para evitar //
        urlFuente = urlDinamica.replaceAll("/$", "");
        break;
      case ESTATICA:
        System.out.println("Propagando baja a fuente ESTATICA");
        urlFuente = urlEstatica.replaceAll("/$", "");
        break;
      case PROXY:
        urlFuente = urlProxy.replaceAll("/$", "");
        break;
      default:
        throw new IllegalArgumentException("Origen desconocido: " + hecho.getOrigen().getProvieneDe());
    }
    //pasar autenticacion si es necesaria
      webClientBuilder.baseUrl(urlFuente).build()
          .put()
          .uri("/hechos/" + hecho.getTitulo() + "/ocultar")
          .header("Authorization", "Bearer " + authentication.getCredentials())
          .retrieve()
          .toBodilessEntity()
          .subscribe(); // Lo hacemos asíncrono para no trabar
    System.out.println("Propagación de baja enviada a la fuente: " + urlFuente);
  }

  @Override
  public SolicitudEliminacionAgregadorOutputDTO evaluarSolicitud(Integer nroSolicitud, boolean aceptado, Authentication authentication) {
    var solicitud = solicitudRepository.findById(nroSolicitud).orElse(null);
    if (solicitud == null) {
      throw new IllegalArgumentException("No existe la solicitud con nro: " + nroSolicitud);
    }

    if (solicitud.getEstado() == Estados.ACEPTADO || solicitud.getEstado() == Estados.RECHAZADO) {
      throw new IllegalStateException("La solicitud ya ha sido procesada.");
    }

    Estados estado = aceptado ? Estados.ACEPTADO : Estados.RECHAZADO;

    System.out.println(estado);

    solicitud.setEstado(estado);

    solicitudRepository.save(solicitud);


    RegistroCambioEstado registroCambioEstados = new RegistroCambioEstado(
        estado,
        aceptado ? "Solicitud aceptada por el revisor" : "Solicitud rechazada por el revisor",
        solicitud
    );
    registroCambioEstadosRepository.save(registroCambioEstados);

    if(estado == Estados.ACEPTADO) {
    // 2. Ocultar localmente en el Agregador (para inmediatez)
      Hecho hecho = solicitud.getUnHecho();
      hecho.ocultar();
      hechoRepository.save(hecho);
    // 3. PROPAGAR A LA FUENTE ORIGINAL (Lo que pidió el corrector)
      propagarBajaAFuente(hecho, authentication);
    // 4. ELIMINAR HECHO DE LA COLECCION
      coleccionService.eliminarHechoDeColecciones(hecho);
    }

    return this.solicitudOutputDTO(solicitud);
  }

  @Override
  public List<SolicitudEliminacionAgregadorOutputDTO> mostrarSolicitudes() {
    List<SolicitudEliminacion> solicitudes = solicitudRepository.findAll();

    return solicitudes.stream()
        .map(this::solicitudOutputDTO)
        .toList();
  }

  public SolicitudEliminacionAgregadorOutputDTO solicitudOutputDTO(SolicitudEliminacion solicitud) {
    var solicitudOutput = new SolicitudEliminacionAgregadorOutputDTO();

    solicitudOutput.setNroDeSolicitud(solicitud.getNroSolicitud());
    solicitudOutput.setFechaCreacionSolicitud(solicitud.getFechaCreacionSolicitud());
    solicitudOutput.setIdDelHecho(solicitud.getUnHecho().getId());
    solicitudOutput.setNombreHecho(solicitud.getUnHecho().getTitulo());
    solicitudOutput.setMotivo(solicitud.getMotivo());
    solicitudOutput.setEstado(solicitud.getEstado().name());

    return solicitudOutput;
  }

  public List<RegistroCambioEstado> getRegistroCambioEstadosPorID(Integer nro) {
    return solicitudRepository.findById(nro)
        .map(SolicitudEliminacion::getRegistroCambioEstado) // o .map(solicitud -> solicitud.getRegistroCambioEstado())
        .orElseThrow(() -> new ResourceNotFoundException("Solicitud con número " + nro + " no encontrada."));
  }

  public List<RegistroEstadoOutputDTO> obtenerTodosRegistroCambioEstados() {

    System.out.println("Obteniendo todos los registros de cambio de estado de solicitudes...");
    return registroCambioEstadosRepository.findAll().stream().map(r->this.registroEstadoOutputDTO(r)).collect(Collectors.toList());
  }

  public List<SolicitudEliminacionAgregadorOutputDTO> buscarPorSolicitante(String email) {
    return solicitudRepository.findBySolicitanteId(email)
        .stream().map(this::solicitudOutputDTO).toList();
  }

  private RegistroEstadoOutputDTO registroEstadoOutputDTO(RegistroCambioEstado registro) {
    RegistroEstadoOutputDTO dto = new RegistroEstadoOutputDTO();
    dto.setIdRegistroEstado(registro.getId());
    dto.setEstado(registro.getEstado().name());
    dto.setFechaModificacion(registro.getFechaModificacion());
    dto.setDescripcion(registro.getDescripcion());
    if (registro.getModificador() != null) {
      dto.setModificadoPor(registro.getModificador().getUserId());
    } else {
      dto.setModificadoPor("Sistema");
    }
    dto.setIdSolicitud(registro.getSolicitudEliminacion().getNroSolicitud());
    return dto;
  }

}














