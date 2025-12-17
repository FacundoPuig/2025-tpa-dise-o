package ar.utn.ba.dsi.servicioAgregador.services.impl;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.ApiResponse;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Categoria;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.ICategoriaRepository;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IFuenteRepository;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IHechoAgregacionRepository;
import ar.utn.ba.dsi.servicioAgregador.services.IHechoService;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Etiqueta;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HechoService implements IHechoService {

  @Autowired
  private IHechoAgregacionRepository hechoAgregacionRepository;
  @Autowired
  private final WebClient.Builder webClientBuilder;
  @Autowired
  private NormalizacionService normalizacionService;
  @Autowired
  private IHechoAgregacionRepository hechoRepository;
  @Autowired
  private IFuenteRepository fuenteRepository;

  @Autowired
  private ICategoriaRepository categoriaRepository;

  @Value("${proxy.ruta}")
  private String rutaProxy;

  @Value("${dinamica.ruta}")
  private String rutaDinamica;

  public HechoService(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

	@Override
  public HechoAgregadorOutputDTO hechoAgregadorOutputDTO(Hecho hecho) {
    HechoAgregadorOutputDTO dto = new HechoAgregadorOutputDTO();

    // 1. Identificador (CRÍTICO para que funcione el click en detalle)
    dto.setId(hecho.getId());

    // 2. Datos Básicos
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento());

    // 3. Datos Multimedia y Carga (Faltaban en tu snippet)
    dto.setFechaCarga(hecho.getFechaCarga());
    dto.setContenidoMultimedia(hecho.getContenidoMultimedia());

    // 4. Categoría
    if(hecho.getCategoria() != null) {
      dto.setCategoria(hecho.getCategoria().getNombre());
    }

    // 5. Ubicación
    if(hecho.getUbicacion() != null) {
      dto.setLatitud(hecho.getUbicacion().getLatitud());
      dto.setLongitud(hecho.getUbicacion().getLongitud());
      dto.setProvincia(hecho.getUbicacion().getProvincia()); // Si lo tienes
    }

    // 6. ORIGEN (TU ARREGLO ✅)
    if(hecho.getOrigen() != null) {
      dto.setNombreOrigen(hecho.getOrigen().getNombre());
      // Validación extra por si el Enum es null (raro pero posible)
      if (hecho.getOrigen().getProvieneDe() != null) {
        dto.setProvieneDeOrigen(hecho.getOrigen().getProvieneDe().name());
      }
    }

    // 7. Etiquetas (Para que no llegue null al front)
    if (hecho.getEtiquetas() != null) {
      dto.setNombreEtiquetas(hecho.getEtiquetas().stream()
          .map(Etiqueta::getNombre)
          .toList());
    } else {
      dto.setNombreEtiquetas(new ArrayList<>());
    }

    return dto;
  }


  @Override
  public List<HechoAgregadorOutputDTO> conseguirTodosLosHechos() {
    return hechoAgregacionRepository.findAll().stream().map(this::hechoAgregadorOutputDTO).collect(Collectors.toList());
  }

  @Override
  public HechoAgregadorOutputDTO obtenerHechoPorTitulo(String titulo) {
    Hecho hecho = hechoAgregacionRepository.findByTitulo(titulo);
    if (hecho == null) {
      return null;
    } else {
      return hechoAgregadorOutputDTO(hecho);
    }
  }

  @Override
  public HechoAgregadorOutputDTO obtenerHechoPorId(Long id) {
    Hecho hecho = hechoAgregacionRepository.findById(id).orElse(null);

    System.out.println("hecho obbtenido " + hecho.getTitulo());

    if (hecho == null) {
      return null;
    } else {
      return hechoAgregadorOutputDTO(hecho);
    }
  }

  @Override
  public List<String> listarCategorias() {
    return normalizacionService.obtenerCategoriasDisponibles();
  }

  public List<HechoAgregadorOutputDTO> buscarHechosPorUsuario(String userId) {
    return webClientBuilder.baseUrl(rutaDinamica).build()
        .get()
        .uri("/hechos/usuario/" + userId)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<HechoAgregadorOutputDTO>>>() {})
        .map(ApiResponse::getDatos)
        .block();
  }

  @Override
  //@Transactional
  public void guardarHechosMasivos(List<Hecho> hechosNuevos) {
    if (hechosNuevos == null || hechosNuevos.isEmpty()) {
      System.out.println("⚠️ guardarHechosMasivos recibió una lista vacía/nula.");
      return;
    }

    // 1. Cargar mapa de categorías (igual que antes)
    List<Categoria> categoriasBD = categoriaRepository.findAll();
    Map<String, Categoria> mapaCategorias = new HashMap<>();
    for (Categoria cat : categoriasBD) {
      mapaCategorias.put(cat.getNombre().toLowerCase().trim(), cat);
    }

    int guardados = 0;
    int errores = 0;

    for (Hecho hecho : hechosNuevos) {
      try {

        // A. Gestión de Categoría (Tu lógica existente)
        if (hecho.getCategoria() != null) {
          String key = hecho.getCategoria().getNombre().toLowerCase().trim();
          if (mapaCategorias.containsKey(key)) {
            hecho.setCategoria(mapaCategorias.get(key));
          } else {
            // Si es nueva, intentar guardarla primero para tener ID
            try {
              Categoria nuevaCat = categoriaRepository.save(hecho.getCategoria());
              mapaCategorias.put(key, nuevaCat);
              hecho.setCategoria(nuevaCat);
            } catch (Exception e) {
              System.err.println("   Error guardando categoría nueva: " + e.getMessage());
            }
          }
        }

        // B. Gestión de Origen (CRÍTICO: Evitar duplicados)
        // Si no tienes lógica aquí, Hibernate intentará crear 'ESTATICA' de nuevo y fallará por unique constraint
        // Por ahora, asumamos que Cascade lo maneja, pero si falla, miraremos aquí.

        // C. Guardado del Hecho
        // 1. Obtener el nombre del origen del hecho nuevo.
        String nombreOrigen = hecho.getOrigen() != null ? hecho.getOrigen().getNombre() : null;

        // 2. BUSCAR POR TÍTULO Y ORIGEN
        Hecho existente = hechoRepository.findByTituloAndOrigen_Nombre(hecho.getTitulo(), nombreOrigen);

        if (existente == null) {
          hechoRepository.save(hecho);
          guardados++;
        } else {
          //System.out.println("   -> Ya existe. Omitiendo/Actualizando.");
        }

      } catch (Exception e) {
        errores++;
        System.err.println("❌ ERROR FATAL guardando hecho '" + hecho.getTitulo() + "':");
        e.printStackTrace();
      }
    }
    System.out.println("✅ Fin proceso masivo. Guardados: " + guardados + ", Errores: " + errores);
  }

  @Override
  public void ocultarHecho(String hechoName) {
    Hecho hecho = hechoAgregacionRepository.findByTitulo(hechoName);
    if (hecho != null) {
      hecho.setVisible(false);
      hechoAgregacionRepository.save(hecho);
    }

  }

}
