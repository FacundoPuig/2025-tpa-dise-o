package ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "edicion")
public class Edicion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hechoOriginal_id", nullable = false)
	@JsonIgnore
	private Hecho idHechoOriginal;

	@Column(name = "tituloPropuesto")
	private String tituloPropuesto;

	@Column(name = "descripcionPropuesta", columnDefinition = "TEXT")
	private String descripcionPropuesta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoriaPropuesta_id")
	private Categoria categoriaPropuesta;

	@Column(name= "latitudPropuesta")
	private Double latitudPropuesta;

	@Column(name= "longitudPropuesta")
	private Double longitudPropuesta;

	@Column(name = "fechaAcontecimientoPropuesta")
	private LocalDateTime fechaAcontecimientoPropuesta;

	@Column(name="contenidoMultimediaPropuesto", length = 255)
	private String contenidoMultimediaPropuesto;

	@Column(name = "visualizador_editor_id")
	private String visualizadorEditorId;

	@Column(name = "fechaEdicion", nullable = false)
	private LocalDate fechaEdicion;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false)
	private EstadoEdicion estado;

	private String detalle;
}