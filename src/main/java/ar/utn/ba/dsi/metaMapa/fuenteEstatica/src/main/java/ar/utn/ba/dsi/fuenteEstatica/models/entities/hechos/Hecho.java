package ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "titulo", nullable = false, length = 200 )
	private String titulo;

	@Column(name = "descripcion", columnDefinition = "TEXT")
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ubicacion_id", referencedColumnName = "id")
	private Ubicacion ubicacion;

	@Column(name = "fechaAcontecimiento")
	private LocalDateTime fechaAcontecimiento;

	@Column(name = "fechaCarga", nullable = false)
	private LocalDate fechaCarga = LocalDate.now();

	@Column(name = "contenidoMultimedia", length = 255)
	private String contenidoMultimedia;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "origen_id", nullable = false)
	private Origenes origen;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(
			name = "hecho_etiqueta",
			joinColumns = @JoinColumn(name = "hecho_id"),
			inverseJoinColumns = @JoinColumn(name = "etiqueta_id",referencedColumnName = "id")
	)
	private List<Etiqueta> etiquetas = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "estadoRevision", length = 50)
	private EstadoRevision estadoRevision;

	@Column(name = "visible", nullable = false)
	private boolean visible = true;

	@Column(name = "enviado", nullable = false)
	private boolean enviado =  false;

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

	public boolean fueCreadoHaceMenosDeDias(Integer dias) {
		LocalDate fechaLimite = LocalDate.now().minusDays(dias);
		return this.fechaCarga.isAfter(fechaLimite);
	}

	public void ocultar() {
		this.visible = false;
	}

	public void agregarEtiqueta(String nombre, String descripcion) {
		this.etiquetas.add(new Etiqueta(nombre, descripcion));
	}
}
