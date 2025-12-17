package ar.utn.ba.dsi.servicioUsuarios.services;

import ar.utn.ba.dsi.servicioUsuarios.models.dtos.UserRolesPermissionsDTO;
import ar.utn.ba.dsi.servicioUsuarios.models.entities.RolUsuario;
import ar.utn.ba.dsi.servicioUsuarios.models.entities.UsuarioFisico;
import ar.utn.ba.dsi.servicioUsuarios.models.entities.Visualizador;
import ar.utn.ba.dsi.servicioUsuarios.models.repositories.IVisualizadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.output.AuthResponseDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginService {

  @Autowired
  private final IVisualizadorRepository visualizadorRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  @Autowired
  private final JwtService jwtService;
  @Autowired
  private final UserDetailsService userDetailsService;

  public LoginService(IVisualizadorRepository visualizadorRepository, JwtService jwtService, UserDetailsService userDetailsService) {
    this.visualizadorRepository = visualizadorRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  public UsuarioFisico autenticarUsuario(String email, String password) throws Exception {
    Visualizador visualizador = visualizadorRepository.findByUsuario_Email(email);

    if (visualizador == null) {
      throw new Exception("Usuario no encontrado");
    }

    UsuarioFisico usuario = visualizador.getUsuario();

    if (!passwordEncoder.matches(password, usuario.getHashContrasenia())) {
      throw new Exception("Contraseña incorrecta");
    }

    return usuario;
  }

  public String generarAccessToken(String email) {
    final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    return jwtService.generateAccessToken(userDetails);
  }

  public String generarRefreshToken(String email) {
    final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    return jwtService.generateRefreshToken(userDetails);
  }

  public UserRolesPermissionsDTO obtenerRolesYPermisosUsuario(String email) throws Exception {
    Visualizador visualizador = visualizadorRepository.findByUsuario_Email(email);

    if (visualizador == null) {
      throw new Exception("Usuario no encontrado con el email: " + email);
    }

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

  public AuthResponseDTO loginAndEnrichData(String email, String password) throws Exception {

    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    if (password != null) {
      if (!passwordEncoder.matches(password, userDetails.getPassword())) {
        throw new Exception("Contraseña incorrecta");
      }
    }

    Visualizador visualizador = visualizadorRepository.findByUsuario_Email(email);
    if (visualizador == null) {
      throw new UsernameNotFoundException("Usuario no encontrado: " + email);
    }
    UsuarioFisico fisico = visualizador.getUsuario();

    String token = (password != null) ? generarAccessToken(userDetails.getUsername()) : null;
    UserRolesPermissionsDTO rolesPermisos = obtenerRolesYPermisosUsuario(email);

    return AuthResponseDTO.builder()
        .token(token)
        .rolesPermisos(rolesPermisos)
        .id(visualizador.getNroIdVisualizador())
        .nombre(fisico.getNombre())
        .apellido(fisico.getApellido())
        .email(fisico.getEmail())
        .fechaDeNacimiento(fisico.getFechaDeNacimiento())
        .build();
  }

  public boolean verificarIdPorEmail(String email, Long visualizadorId) {
    // 1. Buscamos el Visualizador por el email de su UsuarioFisico
    System.out.println("Verificando visualizador con email: " + email + " y ID esperado: " + visualizadorId);
    Visualizador visualizador = visualizadorRepository.findByUsuario_Email(email);
    System.out.println("Visualizador encontrado: " + (visualizador != null ? visualizador.getNroIdVisualizador() : "null"));

    if (visualizador == null) {
      return false; // Email no existe
    }

    // 2. Comprobamos que el ID de la base de datos (nroIdVisualizador) coincida con el ID que se quiere usar
    return visualizador.getNroIdVisualizador() == visualizadorId;
  }
}