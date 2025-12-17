package ar.utn.ba.dsi.servicioEstadisticas.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "estadisticas_por_provincia_y_categoria")
public class EstadisticaPorProvinciaYCategoria {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "provincia",nullable = false)
	private String provincia;

	@Column(name = "categoria",nullable = false)
	private String categoria;

	@Column(name = "cantidadHechos", nullable = false)
	private Long cantidadHechos;

	@OneToMany(mappedBy = "estadisticaPorProvinciaYCategoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<EstadisticaProvinciaYCategoriaData> distribucionData;

	@Column(name="fechaGeneracion", nullable = false)
	private LocalDateTime fechaGeneracion;

	@Column(name="es_ultima", nullable = false)
	private boolean esUltima;
}
