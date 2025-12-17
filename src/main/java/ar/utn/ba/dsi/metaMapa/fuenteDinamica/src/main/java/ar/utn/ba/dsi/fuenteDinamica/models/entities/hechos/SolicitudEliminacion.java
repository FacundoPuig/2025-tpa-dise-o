package ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "solicitud_eliminacion")
public class SolicitudEliminacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	// Relación con el hecho que quieren borrar
	@ManyToOne
	@JoinColumn(name = "hecho_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Hecho hecho;

	@Column(name = "motivo", columnDefinition = "TEXT", nullable = false)
	private String motivo;

	@Column(name = "fecha_solicitud", nullable = false)
	private LocalDate fechaSolicitud;

	@Column(name = "solicitante_id")
	private String solicitanteId;

	// Reusamos EstadoRevision (PENDIENTE, ACEPTADO, RECHAZADO) para no crear otro Enum
	@Enumerated(EnumType.STRING)
	@Column(name = "estado")
	private EstadoRevision estado;

	public SolicitudEliminacion(Hecho hecho, String motivo, String solicitanteId) {
		this.hecho = hecho;
		this.motivo = motivo;
		this.solicitanteId = solicitanteId;
		this.fechaSolicitud = LocalDate.now();
		this.estado = EstadoRevision.PENDIENTE;
	}
}