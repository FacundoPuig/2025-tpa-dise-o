package ar.utn.ba.dsi.servicioEstadisticas.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
@Table(name = "estadisticas_por_provincia")
public class EstadisticaPorProvincia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "provincia", nullable = false)
	private String provincia;

	@Column(name = "coleccion_titulo", nullable = false)
	private String coleccionTitulo;

	@Column(name = "coleccion_handle", nullable = false)
	private String coleccionHandle;

	@Column(name = "cantidad_hechos", nullable = false)
	private Long cantidadHechos;

	@OneToMany(mappedBy = "estadisticaPorProvincia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<EstadisticaProvinciaData> distribucionData;

	@Column(name="fechaGeneracion", nullable = false)
	private LocalDateTime fechaGeneracion;

	@Column(name="es_ultima", nullable = false)
	private boolean esUltima;

}