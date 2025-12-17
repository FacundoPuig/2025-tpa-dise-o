package ar.utn.ba.dsi.servicioUsuarios.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "RolUsuario")
public class RolUsuario {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name="nombrePermiso", nullable = false)
  private String nombrePermiso;

  @ElementCollection(targetClass = Permiso.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "rol_permisos", joinColumns = @JoinColumn(name = "rol_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "permiso", nullable = false)
  private List<Permiso> listaDePermisos;

  public RolUsuario(String nombrePermiso, List<Permiso> listaDePermisos) {
    this.nombrePermiso = nombrePermiso;
    this.listaDePermisos = listaDePermisos;
  }

  public boolean tenesPermisos(Permiso permiso) {
    return this.listaDePermisos.contains(permiso);
  }

  public void cambiarRol(Visualizador visualizador) {
    if(!(this.nombrePermiso == "Administrador")) {
      if (this.cumpleCondicion(visualizador))
        visualizador.setRolUsuario(new RolUsuario("Contribuyente Registrado", Arrays.asList(Permiso.EDITAR_HECHO_PROPIO)));
      else
        visualizador.setRolUsuario(new RolUsuario("Contribuyente Anonimo", Collections.emptyList()));
    }
  }

  private boolean cumpleCondicion(Visualizador visualizador){
    return !visualizador.getUsuario().getEmail().trim().isEmpty(); //tiene email.
  }
}
