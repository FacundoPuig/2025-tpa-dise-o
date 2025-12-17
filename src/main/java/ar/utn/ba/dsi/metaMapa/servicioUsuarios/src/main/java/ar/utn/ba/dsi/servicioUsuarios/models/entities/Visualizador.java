package ar.utn.ba.dsi.servicioUsuarios.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;

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

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "usuario_fisico_id", referencedColumnName = "id", unique = true)
	private UsuarioFisico usuario;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "rol_usuario_id", referencedColumnName = "id")
	private RolUsuario rolUsuario;

	private final static Integer mayoriaEdad = 18;

	public Visualizador(UsuarioFisico user, Boolean admin) {
		this.usuario = user;
		if(admin)
			this.rolUsuario = new RolUsuario("ADMIN", Arrays.asList(
					Permiso.CREAR_COLECCION,
					Permiso.MODIFICAR_CRITERIO_COLECCION,
					Permiso.ACEPTAR_SOLICITUD_ELIMINACION,
					Permiso.RECHAZAR_SOLICITUD_ELIMINACION,
					Permiso.ELIMINAR_COLECCION,
					Permiso.EDITAR_COLECCION,
					Permiso.ETIQUETAR_HECHO,
					Permiso.REVISAR_HECHO,
					Permiso.VER_SOLICITUDES,
					Permiso.ELIMINAR_HECHO
			));
		else
			// Un usuario que se registra NO ES Visitante, es Contribuyente
			this.rolUsuario =  new RolUsuario("CONTRIBUTOR", Arrays.asList(
					Permiso.EDITAR_HECHO_PROPIO
			));
	}

	public Boolean esMayorDeEdad() {
		return this.edad() >= mayoriaEdad;
	}

	public int edad(){
		LocalDate hoy = LocalDate.now();
		return Period.between(usuario.getFechaDeNacimiento(), hoy).getYears();
	}

	public boolean estaRegistrado(){
		return !(this.getUsuario() == null);
	}
}