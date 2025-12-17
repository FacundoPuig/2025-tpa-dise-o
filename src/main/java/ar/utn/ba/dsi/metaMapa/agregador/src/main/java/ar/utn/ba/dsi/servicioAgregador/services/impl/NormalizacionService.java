package ar.utn.ba.dsi.servicioAgregador.services.impl;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;

@Service
public class NormalizacionService {

	private final Map<String, Provincia> provinciasMap = new HashMap<>();
	private final Map<String, String> categoriasNormalizadasMap = new HashMap<>();
	private final WebClient webClientGeoref;

	// --- NUEVO: Instancia estática para acceso global desde Filtros ---
	public static NormalizacionService instance;

	public NormalizacionService(WebClient.Builder webClientBuilder) {
		this.webClientGeoref = webClientBuilder.baseUrl("https://apis.datos.gob.ar/georef/api").build();
	}

	@PostConstruct
	public void init() {
		this.inicializarProvincias();
		this.inicializarCategorias();
		// Guardamos la referencia estática al arrancar
		instance = this;
	}

	// --- MÉTODO ESTÁTICO PARA EL FILTRO ---
	public static String getCategoriaNormalizada(String nombreRaw) {
		if (instance == null || nombreRaw == null) return nombreRaw;

		String nombreLimpio = nombreRaw;

		// 1. INTENTAR DECODIFICAR URL (Transforma Inundaci%C3%B3n -> Inundación)
		try {
			// Si tiene %, es muy probable que esté encodeado
			if (nombreRaw.contains("%")) {
				nombreLimpio = URLDecoder.decode(nombreRaw, StandardCharsets.UTF_8);
			}
		} catch (Exception e) {
			// Si falla, seguimos con el original
			System.err.println("Error decodificando: " + nombreRaw);
		}

		// 2. Normalizamos la entrada (quitamos tildes, mayúsculas)
		// Ahora key será "inundacion" en lugar de "inundacic3b3n"
		String key = instance.normalizeKey(nombreLimpio);

		// 3. Buscamos en el mapa.
		return instance.categoriasNormalizadasMap.getOrDefault(key, nombreLimpio);
	}

	public Mono<Hecho> normalizar(Hecho hechoBruto) {
		if (hechoBruto == null) return Mono.empty();
		return normalizarUbicacion(hechoBruto.getUbicacion())
				.map(ubicacionNormalizada -> {
					hechoBruto.setUbicacion(ubicacionNormalizada);
					// También normalizamos la categoría en el objeto por si acaso
					Categoria catNorm = normalizarCategoria(hechoBruto.getCategoria());
					hechoBruto.setCategoria(catNorm);
					return hechoBruto;
				});
	}

	private Mono<Ubicacion> normalizarUbicacion(Ubicacion ubicacion) {
		if (ubicacion == null) return Mono.empty();

		if (ubicacion.getProvincia() != null && !ubicacion.getProvincia().isBlank() && !"Desconocida".equals(ubicacion.getProvincia())) {
			String key = normalizeKey(ubicacion.getProvincia());
			Provincia p = provinciasMap.get(key);
			if (p == null) p = provinciasMap.get(ubicacion.getProvincia().trim());
			if (p != null) aplicarDatosProvincia(ubicacion, p);
			return Mono.just(ubicacion);
		}
		else if (ubicacion.getLatitud() != null && ubicacion.getLongitud() != null) {
			return getProvinciaFromGeorefApi(ubicacion.getLatitud(), ubicacion.getLongitud())
					.map(nombreProvincia -> {
						if (nombreProvincia != null) {
							String key = normalizeKey(nombreProvincia);
							Provincia p = provinciasMap.get(key);
							if (p != null) aplicarDatosProvincia(ubicacion, p);
							else ubicacion.setProvincia(nombreProvincia);
						}
						return ubicacion;
					})
					.defaultIfEmpty(ubicacion);
		}
		return Mono.just(ubicacion);
	}

	private void aplicarDatosProvincia(Ubicacion u, Provincia p) {
		u.setProvincia(p.getNombre());
		if (u.getLatitud() == null || u.getLatitud() == 0.0) u.setLatitud(p.getCentroide().getLat());
		if (u.getLongitud() == null || u.getLongitud() == 0.0) u.setLongitud(p.getCentroide().getLon());
	}

	private Mono<String> getProvinciaFromGeorefApi(Double latitud, Double longitud) {
		String uri = String.format(Locale.US, "/ubicacion?lat=%f&lon=%f", latitud, longitud);
		return this.webClientGeoref.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(GeorefUbicacionResponse.class)
				.map(response -> {
					if (response != null && response.ubicacion != null && response.ubicacion.provincia != null) {
						return response.ubicacion.provincia.nombre;
					}
					return "";
				})
				.onErrorResume(e -> Mono.empty());
	}

	public Categoria normalizarCategoria(Categoria categoriaBruta) {
		if (categoriaBruta == null || categoriaBruta.getNombre() == null) return null;
		String key = normalizeKey(categoriaBruta.getNombre());
		String normalizado = categoriasNormalizadasMap.get(key);
		return (normalizado != null) ? new Categoria(normalizado) : categoriaBruta;
	}

	private void inicializarProvincias() {
		try {
			ObjectMapper mapper = new ObjectMapper();

			// INTENTO 1: Raíz
			InputStream inputStream = getClass().getResourceAsStream("/provincias.json");

			// INTENTO 2: Carpeta data
			if (inputStream == null) {
				inputStream = getClass().getResourceAsStream("/data/provincias.json");
			}

			if (inputStream == null) {
				System.err.println("⚠️ ADVERTENCIA: No se encontró 'provincias.json'. La normalización geográfica local no funcionará.");
				return;
			}

			ProvinciasFile file = mapper.readValue(inputStream, ProvinciasFile.class);
			List<Provincia> lista = Collections.emptyList();
			if (file != null && file.getProvincias() != null && !file.getProvincias().isEmpty()) {
				ProvinciasGroup g = file.getProvincias().get(0);
				if (g != null && g.getProvincias() != null) lista = g.getProvincias();
			}
			for (Provincia p : lista) {
				putIfNotNull(normalizeKey(p.getNombre()), p);
				putIfNotNull(normalizeKey(p.getNombre_completo()), p);
				putIfNotNull(normalizeKey(p.getIso_nombre()), p);
				putIfNotNull(normalizeKey(p.getIso_id()), p);
				putIfNotNull(p.getId(), p);
			}
			System.out.println("✅ Provincias cargadas correctamente.");
		} catch (Exception e) {
			System.err.println("Error cargando provincias: " + e.getMessage());
		}
	}

	private void inicializarCategorias() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			// Buscamos en raíz y en /data
			InputStream inputStream = getClass().getResourceAsStream("/categorias.json");
			if (inputStream == null) inputStream = getClass().getResourceAsStream("/data/categorias.json");

			if (inputStream == null) {
				System.err.println("⚠️ ADVERTENCIA: No se encontró categorias.json");
				return;
			}

			List<CategoriaMapeo> mapeos = mapper.readValue(inputStream, new TypeReference<List<CategoriaMapeo>>() {});
			for (CategoriaMapeo mapeo : mapeos) {
				String nombreNormalizado = mapeo.getNombreNormalizado();
				// Mapeamos el nombre canónico
				categoriasNormalizadasMap.put(normalizeKey(nombreNormalizado), nombreNormalizado);

				// Mapeamos los sinónimos
				if (mapeo.getSinonimos() != null) {
					for (String sinonimo : mapeo.getSinonimos()) {
						categoriasNormalizadasMap.put(normalizeKey(sinonimo), nombreNormalizado);
					}
				}
			}
			System.out.println("✅ Categorías cargadas: " + categoriasNormalizadasMap.size());
		} catch (Exception e) {
			System.err.println("Error cargando categorías: " + e.getMessage());
		}
	}

	private void putIfNotNull(String key, Provincia p) {
		if (key != null && !key.isBlank()) provinciasMap.put(key, p);
	}

	private String normalizeKey(String s) {
		if (s == null) return null;
		String t = s.trim().toLowerCase();
		t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		t = t.replaceAll("[^a-z0-9]", ""); // Agresivo
		return t;
	}

	public List<String> obtenerCategoriasDisponibles() {
		if (categoriasNormalizadasMap.isEmpty()) this.inicializarCategorias();
		return new ArrayList<>(new TreeSet<>(categoriasNormalizadasMap.values()));
	}

	public static class GeorefUbicacionResponse {
		public UbicacionData ubicacion;
		public static class UbicacionData { public ProvinciaData provincia; }
		public static class ProvinciaData { public String nombre; }
	}
}