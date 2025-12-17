package ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rol_usuario")
public class RolUsuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "nombrePermiso",length = 100)
  private String nombrePermiso;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "rol_usuario_permiso", joinColumns = @JoinColumn(name = "rol_usuario_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "permiso", length = 50)
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
        visualizador.setRolUsuario(new RolUsuario("Contribuyente Registrado", List.of(Permiso.EDITAR_HECHO_PROPIO)));
      else
        visualizador.setRolUsuario(new RolUsuario("Contribuyente Anonimo", List.of()));
    }
  }

  private boolean cumpleCondicion(Visualizador visualizador){
    return !visualizador.getUsuario().getEmail().isBlank(); //tiene email.
  }
}
