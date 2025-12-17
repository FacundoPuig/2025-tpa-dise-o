package ar.utn.ba.dsi.servicioAgregador.models.entities.hechos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="hecho")
@EqualsAndHashCode(of = {"titulo", "descripcion", "fechaAcontecimiento", "origen"})
public class Hecho {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name="titulo", length = 200, nullable = false)
	private String titulo;

	@Column(name="descripcion", columnDefinition = "TEXT", nullable = false)  // habia hechos de estatica que tiraban error por descrip muy larga
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ubicacion_id", referencedColumnName = "id")
	private Ubicacion ubicacion;

	@Column(name="fechaAcontecimiento", nullable = false)
	private LocalDateTime fechaAcontecimiento;

	@Column(name="fechaCarga", nullable = false)
	private LocalDate fechaCarga = LocalDate.now();

	@Column(name = "contenidoMultimedia", length = 255)
	private String contenidoMultimedia;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "origen_id", nullable = false)
	private Origenes origen;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(
			name = "hecho_etiqueta",
			joinColumns = @JoinColumn(name = "hecho_id"),
			inverseJoinColumns = @JoinColumn(name = "etiqueta_id",referencedColumnName = "id")
	)
	private List<Etiqueta> etiquetas;

/*	@Column(name = "esConsensuado", nullable = false)
	private Boolean esConsensuado = false;*/


	//AGREGADOR NO LE INTERESA EL ESTADO DE REVISION
//	@Enumerated(EnumType.STRING)
//	@Column(name = "estadoRevision", length = 50)
//	private EstadoRevision estadoRevision;

	@Column(name = "visible", nullable = false)
	private Boolean visible = true;

	public Hecho(String titulo, String descripcion, Categoria categoria, LocalDateTime fechaAcontecimiento, String contenido, Origenes origen, Ubicacion ubicacion) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.ubicacion = ubicacion;
		this.fechaAcontecimiento = fechaAcontecimiento;
		this.contenidoMultimedia = contenido;
		this.origen = origen;

		this.etiquetas = new ArrayList<>();
	}

	public void ocultar() {
		this.visible = false;
	}

	public void agregarEtiqueta(String nombre, String descripcion) {
		this.etiquetas.add(new Etiqueta(nombre, descripcion));
	}

	public boolean esIgual(Hecho otroHecho) {
		if (otroHecho == null) return false;

		// 1. Comparar Título
		boolean mismoTitulo = Objects.equals(this.titulo, otroHecho.getTitulo());

		// 2. Comparar Fecha de ACONTECIMIENTO (¡NO fecha de carga!)
		boolean mismaFecha = Objects.equals(this.fechaAcontecimiento, otroHecho.getFechaAcontecimiento());

		// 3. Comparar Descripción (A veces los espacios en blanco rompen esto, usamos trim)
		String desc1 = this.descripcion != null ? this.descripcion.trim() : "";
		String desc2 = otroHecho.getDescripcion() != null ? otroHecho.getDescripcion().trim() : "";
		boolean mismaDescripcion = desc1.equals(desc2);

		return mismoTitulo && mismaFecha && mismaDescripcion;
	}
}