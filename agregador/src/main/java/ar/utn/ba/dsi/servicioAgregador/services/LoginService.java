/*
package ar.utn.ba.dsi.servicioAgregador.services;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.UserRolesPermissionsDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios.RolUsuario;
import ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios.UsuarioFisico;
import ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios.Visualizador;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.impl.VisualizadorRepository;
import ar.utn.ba.dsi.servicioAgregador.utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoginService {

  private final VisualizadorRepository visualizadorRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public LoginService(VisualizadorRepository visualizadorRepository) {
    this.visualizadorRepository = visualizadorRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public UsuarioFisico autenticarUsuario(String email, String password) throws Exception {
    Optional<UsuarioFisico> usuarioOpt = visualizadorRepository.findUserByEmail(email);

    if (usuarioOpt.isEmpty()) {
      throw new Exception();
    }

    UsuarioFisico usuario = usuarioOpt.get();

    // Verificar la contraseña usando BCrypt
    if (!passwordEncoder.matches(password, usuario.getHashContrasenia())) {
      throw new Exception();
    }

    return usuario;
  }

  public String generarAccessToken(String email) {
    return JwtUtil.generarAccessToken(email);
  }

  public String generarRefreshToken(String email) {
    return JwtUtil.generarRefreshToken(email);
  }

  public UserRolesPermissionsDTO obtenerRolesYPermisosUsuario(String email) throws Exception {
    Visualizador visualizador = visualizadorRepository.findVisualizadorByEmail(email)
        .orElseThrow(() -> new Exception("Usuario no encontrado con el email: " + email));

    RolUsuario rol = visualizador.getRolUsuario();
    UsuarioFisico fisico = visualizador.getUsuario();
    String nombreRol = rol.getNombrePermiso();

    List<String> permisosComoString = rol.getListaDePermisos().stream()
        .map(Enum::name)
        .collect(Collectors.toList());

    return UserRolesPermissionsDTO.builder()
        .email(fisico.getEmail())
        .nombreRol(nombreRol)
        .permisos(permisosComoString)
        .build();
  }
}
*/
