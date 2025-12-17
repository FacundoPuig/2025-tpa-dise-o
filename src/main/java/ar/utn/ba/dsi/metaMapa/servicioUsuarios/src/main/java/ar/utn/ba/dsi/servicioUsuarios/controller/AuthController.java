package ar.utn.ba.dsi.servicioUsuarios.controller;

import ar.utn.ba.dsi.servicioUsuarios.models.dtos.input.LoginRequestDTO;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.input.VisualizadorInputDTO;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.output.AuthResponseDTO;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.output.VisualizadorOutputDTO;
import ar.utn.ba.dsi.servicioUsuarios.services.IVisualizadorService;
import ar.utn.ba.dsi.servicioUsuarios.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import ar.utn.ba.dsi.servicioUsuarios.services.LoginService;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.UserRolesPermissionsDTO;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${frontend.client.url}")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final IVisualizadorService visualizadorService;
	private final LoginService loginService;

	public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtService jwtService, IVisualizadorService visualizadorService, LoginService loginService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtService = jwtService;
		this.visualizadorService = visualizadorService;
		this.loginService = loginService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) throws Exception {
		//Solo se autentica (verifica credenciales). La carga de datos se hace después.
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);

		//Si es exitoso, obtenemos todos los datos dinámicos del LoginService.
		AuthResponseDTO response = loginService.loginAndEnrichData(request.getEmail(), request.getPassword());

		System.out.print("token " + response.getToken());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	public VisualizadorOutputDTO register(@RequestBody VisualizadorInputDTO visualizadorInput) {
		return visualizadorService.crear(visualizadorInput);
	}

	@PutMapping("/user/profile")
	public ResponseEntity<AuthResponseDTO> updateProfile(
			@RequestBody VisualizadorInputDTO visualizadorInput,
			Authentication authentication) {

		String email = authentication.getName();

		try {
			visualizadorService.actualizarPerfil(email, visualizadorInput);

			//Volver a obtener todos los datos para generar el AuthResponseDTO
			// Se usa 'loginAndEnrichData' con password=null para obtener la información actualizada
			// sin requerir la contraseña.
			AuthResponseDTO response = loginService.loginAndEnrichData(email, null);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Endpoint para que el frontend obtenga los roles y permisos
	 * de un usuario ya autenticado (usando su token).
	 */
	@GetMapping("/user/roles-permisos")
	public ResponseEntity<UserRolesPermissionsDTO> getRolesYPermisos(Authentication authentication) {
		String email = authentication.getName();

		try {
			UserRolesPermissionsDTO dto = loginService.obtenerRolesYPermisosUsuario(email);
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Endpoint interno para la comunicación entre servicios (Agregador).
	 * Verifica si un email autenticado está asociado al visualizadorId.
	 */
	@GetMapping("/verify-id")
	public ResponseEntity<Void> verifyUserId(
																						@RequestParam("email") String email,
																						@RequestParam("visualizadorId") Long visualizadorId) {
		System.out.println("llego el request de verificacion para el email: " + email + " y visualizadorId: " + visualizadorId);
		boolean esValido = loginService.verificarIdPorEmail(email, visualizadorId);

		if (esValido) {
			return ResponseEntity.ok().build(); // 200 OK: Match verificado
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 FORBIDDEN: Mismatch
		}
	}


}