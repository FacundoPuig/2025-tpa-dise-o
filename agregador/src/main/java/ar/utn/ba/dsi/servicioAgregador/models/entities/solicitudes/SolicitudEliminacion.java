package ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solicitud_eliminacion")
public class SolicitudEliminacion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long nroSolicitud;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hecho_id")
	private Hecho unHecho;

	@Enumerated(EnumType.STRING)
	@Column(length = 50)
	private Estados estado = Estados.PENDIENTE;

	@Column(name = "motivo", nullable = false, length = 2000)
	private String motivo;

	@Column(name = "fechaCreacionSolicitud", nullable = false)
	private LocalDateTime fechaCreacionSolicitud = LocalDateTime.now();

	// CAMPO NUEVO
	@Column(name = "solicitante_id")
	private String solicitanteId;

	@OneToMany(mappedBy = "solicitudEliminacion", cascade = CascadeType.ALL)
	private List<RegistroCambioEstado> RegistroCambioEstado = new ArrayList<>();

	public SolicitudEliminacion(Hecho hechoAEliminar, String unMotivo, String solicitanteId) {
		this.unHecho = hechoAEliminar;
		this.motivo = unMotivo;
		this.solicitanteId = solicitanteId;
	}

	public void agregarEstado(RegistroCambioEstado cambioEstado) {
		this.RegistroCambioEstado.add(cambioEstado);
	}

	//se rompe el principio de de responsabilidad unica de SOLID
	//se creo una clase validador

//	public boolean validacionSolicitud(){
//		return this.motivo.length() >= this.minCaracteres;
//	}

	public LocalDateTime getFechaUltimaModificacionSolicitud() {
		if (RegistroCambioEstado == null || RegistroCambioEstado.isEmpty()) {
			return null;
		}
		return RegistroCambioEstado.get(this.RegistroCambioEstado.size() - 1).getFechaModificacion(); // último cambio
	}

	public void setFechaUltimaModificacionSolicitud(LocalDateTime fechaUltimaModificacion) {
		if (RegistroCambioEstado != null && !RegistroCambioEstado.isEmpty()) {
			RegistroCambioEstado.get(RegistroCambioEstado.size() - 1).setFechaModificacion(fechaUltimaModificacion);
		}
	}

}
