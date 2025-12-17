package ar.utn.ba.dsi.servicioEstadisticas.controller;

import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.SolicitudSpamDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.ProvinciaHechosPorColeccionListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.CategoriaReportadaListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.ProvinciaHechosPorCategoriaListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.HoraHechosPorCategoriaListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.services.EstadisticaService;
import ar.utn.ba.dsi.servicioEstadisticas.services.GeneradorDeEstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticasController {

	@Autowired
	private EstadisticaService estadisticasService;
	@Autowired
	private GeneradorDeEstadisticasService generadorDeEstadisticasService;

	// --- ENDPOINTS PARA DASHBOARD (Devuelven Distribuciones) ---

	// Métrica 1: Distribución de provincias por colección
	// GET /estadisticas/{handleId}/distribucion-provincias
	@GetMapping("/{handleId}/distribucion-provincias")
	public ResponseEntity<ProvinciaHechosPorColeccionListDTO> getDistribucionProvinciasPorColeccion(@PathVariable String handleId) {
		ProvinciaHechosPorColeccionListDTO dto = estadisticasService.getDistribucionProvinciasPorColeccion(handleId);
		return ResponseEntity.ok(dto);
	}

	// Métrica 2: Distribución de categorías global
	// GET /estadisticas/distribucion-categorias
	@GetMapping("/distribucion-categorias")
	public ResponseEntity<CategoriaReportadaListDTO> getDistribucionCategorias() {
		CategoriaReportadaListDTO dto = estadisticasService.getDistribucionCategorias();
		return ResponseEntity.ok(dto);
	}

	// Métrica 3: Distribución de provincias por categoría
	// GET /estadisticas/distribucion-provincias-por-categoria?categoria={nombre}
	@GetMapping("/distribucion-provincias-por-categoria")
	public ResponseEntity<ProvinciaHechosPorCategoriaListDTO> getDistribucionProvinciasPorCategoria(@RequestParam String categoria) {
		ProvinciaHechosPorCategoriaListDTO dto = estadisticasService.getDistribucionProvinciasPorCategoria(categoria);
		return ResponseEntity.ok(dto);
	}

	// Métrica 4: Distribución de horas por categoría
	// GET /estadisticas/distribucion-horas-por-categoria?categoria={nombre}
	@GetMapping("/distribucion-horas-por-categoria")
	public ResponseEntity<HoraHechosPorCategoriaListDTO> getDistribucionHorasPorCategoria(@RequestParam String categoria) {
		HoraHechosPorCategoriaListDTO dto = estadisticasService.getDistribucionHorasPorCategoria(categoria);
		return ResponseEntity.ok(dto);
	}

	// Métrica 5: Ratio de solicitudes de spam
	// GET /estadisticas/solicitudes-spam-ratio
	@GetMapping("/solicitudes-spam-ratio")
	public ResponseEntity<SolicitudSpamDTO> getSolicitudesSpamRatio() {

		System.out.println("Entrando al endpoint de solicitudes spam ratio...");
		SolicitudSpamDTO dto = estadisticasService.getSolicitudesSpamRatio();

		System.out.println("DTO generado: " + dto.toString());

		return ResponseEntity.ok(dto);
	}

	//Endpoint para hacer el calculo de estadisticas sin el cronjob:)
	@GetMapping("/calcular-todo")
	public ResponseEntity<String> calcularTodas() {
		generadorDeEstadisticasService.calcularEstadisticasPorProvincia();
		generadorDeEstadisticasService.calcularEstadisticasPorCategoria();
		generadorDeEstadisticasService.calcularEstadisticaPorProvinciaYCategoria();
		generadorDeEstadisticasService.calcularEstadisticaPorHoraYCategoria();
		generadorDeEstadisticasService.calcularEstadisticaDeSolicitudesSpam();
		return ResponseEntity.ok("Cálculo de todas las estadísticas forzado exitosamente.");
	}
}
