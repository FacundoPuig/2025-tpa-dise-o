package ar.utn.ba.dsi.servicioEstadisticas.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estadisticas_spam")
public class EstadisticaDeSpam {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cantidad_spam", nullable = false)
	private Long cantidadSpam;

	@Column(name = "total_solicitudes", nullable = false)
	private Long totalSolicitudes;

	@Column(name = "fecha_generacion", nullable = false)
	private LocalDateTime fechaGeneracion;

	@Column(name = "es_ultima", nullable = false)
	private boolean esUltima;
}
