package ar.utn.ba.dsi.servicioUsuarios.services;

import ar.utn.ba.dsi.servicioUsuarios.models.entities.Visualizador;
import ar.utn.ba.dsi.servicioUsuarios.models.repositories.IVisualizadorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final IVisualizadorRepository visualizadorRepository;

	public UserDetailsServiceImpl(IVisualizadorRepository visualizadorRepository) {
		this.visualizadorRepository = visualizadorRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Visualizador visualizador = visualizadorRepository.findByUsuario_Email(email);
		if (visualizador == null) {
			throw new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
		}

		// 1. Obtener el Rol del Visualizador
		String nombreRol = visualizador.getRolUsuario().getNombrePermiso();

		// 2. Crear una lista de Authorities de Spring Security
		List<GrantedAuthority> authorities = new ArrayList<>();

		// 3. Añadir el Rol principal (Spring lo espera con el prefijo "ROLE_")
		// NOTA: Tu frontend espera "ROLE_ADMIN" o "ROLE_CONTRIBUTOR"
		// Usamos el rol que viene del DTO: "Administrador" o "Visitante" (o el que corresponda)
		// Vamos a estandarizar a un prefijo simple para que 'authenticated()' funcione:
		authorities.add(new SimpleGrantedAuthority("ROLE_" + nombreRol.toUpperCase()));

		// 4. Agregar los permisos específicos (opcional, pero buena práctica)
		visualizador.getRolUsuario().getListaDePermisos().forEach(permiso -> {
			authorities.add(new SimpleGrantedAuthority(permiso.name()));
		});

		String password = visualizador.getUsuario().getHashContrasenia();

		// 5. Devolver el objeto User con los roles/permisos
		return new User(email, password, authorities);
	}
}