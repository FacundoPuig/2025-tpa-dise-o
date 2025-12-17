package ar.utn.ba.dsi.servicioAgregador.controller;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.ApiResponse;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.SolicitudEliminacionAgregadorInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.RegistroEstadoOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.SolicitudEliminacionAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.services.impl.SolicitudEliminacionService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@CrossOrigin(origins = "${frontend.client.url}")
public class SolicitudController {

	@Autowired
	private SolicitudEliminacionService solicitudEliminacionService;

	// Constructor limpio (Sin repositorio de usuarios)
	public SolicitudController(SolicitudEliminacionService solicitudEliminacionService) {
		this.solicitudEliminacionService = solicitudEliminacionService;
	}

	// 1. CREAR
	@PostMapping("")
	public ResponseEntity<ApiResponse<SolicitudEliminacionAgregadorOutputDTO>> crearSolicitud(
			@RequestBody SolicitudEliminacionAgregadorInputDTO input,
			@Nullable Authentication authentication) {

		String userId = (authentication != null) ? authentication.getName() : "Anonimo";
		SolicitudEliminacionAgregadorOutputDTO creada = solicitudEliminacionService.crear(input, userId);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse<>(201, "OK", "Solicitud creada", creada));
	}

	// 2. VER TODAS (Admin)
	@GetMapping("")
	public ResponseEntity<ApiResponse<List<SolicitudEliminacionAgregadorOutputDTO>>> mostrarSolicitudes() {
		// La seguridad del rol ADMIN la maneja SecurityConfig
		List<SolicitudEliminacionAgregadorOutputDTO> lista = solicitudEliminacionService.mostrarSolicitudes();
		return ResponseEntity.ok(new ApiResponse<>(200, "OK", "Todas las solicitudes", lista));
	}

	// 3. VER MIS SOLICITUDES (Contribuyente)
	@GetMapping("/mis-solicitudes")
	public ResponseEntity<ApiResponse<List<SolicitudEliminacionAgregadorOutputDTO>>> misSolicitudes(Authentication authentication) {
		String emailUsuario = authentication.getName();
		List<SolicitudEliminacionAgregadorOutputDTO> lista = solicitudEliminacionService.buscarPorSolicitante(emailUsuario);
		return ResponseEntity.ok(new ApiResponse<>(200, "OK", "Mis solicitudes", lista));
	}

	// 4. VER UNA POR ID (Corregido: Sin validación de DB local)
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<SolicitudEliminacionAgregadorOutputDTO>> mostrarSolicitudporID(@PathVariable("id") Integer id) {
		// Asumimos que si tiene permiso para entrar al endpoint (por token), puede verla.
		// Si querés validar que sea suya o sea admin, deberías hacerlo comparando con el token, no con la DB.
		System.out.println("Buscando solicitud con ID: " + id);
		SolicitudEliminacionAgregadorOutputDTO solicitud = solicitudEliminacionService.buscarPorId(id);

		if (solicitud == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>(404, "ERROR", "Solicitud no encontrada", null));
		}

		return ResponseEntity.ok(new ApiResponse<>(200, "OK", "Detalle solicitud", solicitud));
	}

	// 5. ACEPTAR / RECHAZAR (Admin)
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<SolicitudEliminacionAgregadorOutputDTO>> editarSolicitud(
			@PathVariable("id") Integer id,
			@RequestParam("aceptado") Boolean aceptado,
			Authentication authentication) {

		SolicitudEliminacionAgregadorOutputDTO resultado;

		resultado = solicitudEliminacionService.evaluarSolicitud(id, aceptado, authentication);

		return ResponseEntity.ok(new ApiResponse<>(200, "OK", "Solicitud procesada", resultado));
	}

	// 6. HISTORIAL
	@GetMapping("/registros")
	public ResponseEntity<List<RegistroEstadoOutputDTO>> obtenerRegistrosDeSolicitudes() {
		System.out.println("Obteniendo todos los registros de cambio de estado de solicitudes...");
		return ResponseEntity.ok(solicitudEliminacionService.obtenerTodosRegistroCambioEstados());
	}
}