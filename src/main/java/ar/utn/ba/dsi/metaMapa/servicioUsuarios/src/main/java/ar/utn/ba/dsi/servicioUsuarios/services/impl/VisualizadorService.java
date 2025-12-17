package ar.utn.ba.dsi.servicioUsuarios.services.impl;

import ar.utn.ba.dsi.servicioUsuarios.models.dtos.input.VisualizadorInputDTO;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.output.VisualizadorOutputDTO;
import ar.utn.ba.dsi.servicioUsuarios.models.entities.UsuarioFisico;
import ar.utn.ba.dsi.servicioUsuarios.models.entities.Visualizador;
import ar.utn.ba.dsi.servicioUsuarios.models.repositories.IVisualizadorRepository;
import ar.utn.ba.dsi.servicioUsuarios.services.IVisualizadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisualizadorService implements IVisualizadorService {

	@Autowired
	private IVisualizadorRepository visualizadorRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public VisualizadorService(IVisualizadorRepository visualizadorRepository, PasswordEncoder passwordEncoder) {
		this.visualizadorRepository = visualizadorRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public VisualizadorOutputDTO crear(VisualizadorInputDTO visualizadorInput){

		String contraseniaHasheada = passwordEncoder.encode(visualizadorInput.getContrasenia());

		Visualizador nuevoVisualizador = new Visualizador(new UsuarioFisico(
				visualizadorInput.getNombre(),
				visualizadorInput.getApellido(),
				visualizadorInput.getFechaDeNacimiento(),
				visualizadorInput.getEmail(),
				contraseniaHasheada),
				visualizadorInput.getAdmin());

		Visualizador visualizadorGuardado = this.visualizadorRepository.save(nuevoVisualizador);
		System.out.println(contraseniaHasheada);
		return this.visualizadorOutputDTO(visualizadorGuardado);
	}

	public VisualizadorOutputDTO visualizadorOutputDTO(Visualizador nuevoVisualizador){
		VisualizadorOutputDTO visualizadorOutput = new VisualizadorOutputDTO();

		visualizadorOutput.setId(String.valueOf(nuevoVisualizador.getNroIdVisualizador()));
		visualizadorOutput.setNombre(nuevoVisualizador.getUsuario().getNombre());
		visualizadorOutput.setApellido(nuevoVisualizador.getUsuario().getApellido());
		visualizadorOutput.setFechaDeNacimiento(nuevoVisualizador.getUsuario().getFechaDeNacimiento());
		visualizadorOutput.setEdad(nuevoVisualizador.edad());
		visualizadorOutput.setRolUsuario(nuevoVisualizador.getRolUsuario());

		return visualizadorOutput;
	}

	@Override
	public List<VisualizadorOutputDTO> buscarTodas() {
		return this.visualizadorRepository
				.findAll()
				.stream()
				.map(this::visualizadorOutputDTO)
				.collect(Collectors.toList());
	}

	@Override
	public VisualizadorOutputDTO actualizarPerfil(String email, VisualizadorInputDTO visualizadorInput) throws Exception {
		// Buscamos el Visualizador por el email que viene del JWT (Authentication)
		Visualizador visualizador = visualizadorRepository.findByUsuario_Email(email);

		if (visualizador == null) {
			throw new Exception("Visualizador no encontrado con el email: " + email);
		}

		UsuarioFisico usuario = visualizador.getUsuario();

		// Actualizar solo los campos de perfil permitidos
		if (visualizadorInput.getNombre() != null) {
			usuario.setNombre(visualizadorInput.getNombre());
		}
		if (visualizadorInput.getApellido() != null) {
			usuario.setApellido(visualizadorInput.getApellido());
		}
		if (visualizadorInput.getFechaDeNacimiento() != null) {
			usuario.setFechaDeNacimiento(visualizadorInput.getFechaDeNacimiento());
		}

		Visualizador visualizadorActualizado = this.visualizadorRepository.save(visualizador);

		return this.visualizadorOutputDTO(visualizadorActualizado);
	}
}