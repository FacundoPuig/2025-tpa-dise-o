package ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "visualizador")
public class Visualizador {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long nroIdVisualizador;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_fisico_id", referencedColumnName = "id")
	private UsuarioFisico usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rol_usuario_id", referencedColumnName = "id")
	private RolUsuario rolUsuario;

	private String userId;

	private final static Integer mayoriaEdad = 18;

	public Visualizador(UsuarioFisico user, Boolean admin) {
		this.usuario = user;
		if(admin)
			this.rolUsuario = new RolUsuario("Administrador", List.of(
					Permiso.CREAR_COLECCION,
					Permiso.MODIFICAR_CRITERIO_COLECCION,
					Permiso.ACEPTAR_SOLICITUD_ELIMINACION,
					Permiso.RECHAZAR_SOLICITUD_ELIMINACION,
					Permiso.ELIMINAR_COLECCION,
					Permiso.ETIQUETAR_HECHO));
		else
			this.rolUsuario =  new RolUsuario("Visitante", List.of());

		if (this.estaRegistrado()) {
			this.userId = String.valueOf(nroIdVisualizador);
		}

	}

	public Boolean esMayorDeEdad() {
		return this.edad() >= mayoriaEdad;
	}

	public int edad(){
		LocalDate hoy = LocalDate.now();
		return Period.between(usuario.getFechaDeNacimiento(), hoy).getYears();
	}

	//TODO preguntar si los anonimos tienen id. Si no tienen, dejar este codigo
	public boolean estaRegistrado(){
		return !(this.getUsuario() == null);
	}

	//SOLO ADMIN//
	/*public void asignarEtiqueta (Hecho hecho, String etiqueta){
		hecho.agregarEtiqueta(etiqueta);
	}*/

}