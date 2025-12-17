package ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Hecho {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "titulo", nullable = false, length = 200 )
	private String titulo;

	@Column(name = "descripcion", columnDefinition = "TEXT")
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
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

	@ManyToOne(fetch = FetchType.LAZY)
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

	@Column(name = "enviado", nullable = false)
	private boolean enviado;

	@OneToMany(mappedBy = "idHechoOriginal", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Edicion> ediciones = new ArrayList<>();

	@Column(name = "visualizador_creador_id")
	private String visualizadorCreadorId;
	private boolean visible = true;

	@Column(name = "sugerencia_admin", length = 1000)
	private String sugerenciaAdmin;

	public Hecho(String titulo, String descripcion, Categoria categoria, LocalDateTime fechaAcontecimiento, String contenido, Origenes origen, Ubicacion ubicacion) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.ubicacion = ubicacion;
		this.fechaAcontecimiento = fechaAcontecimiento;
		this.contenidoMultimedia = contenido;
		this.origen = origen;
		this.enviado = false;
	}

	public void ocultar() {
		this.visible = false;
	}

	public boolean fueCreadoHaceMenosDeDias(Integer dias) {
		LocalDate fechaLimite = LocalDate.now().minusDays(dias);
		return this.fechaCarga.isAfter(fechaLimite);
	}

	public void agregarEtiqueta(String nombre, String descripcion) {
		Etiqueta etiqueta = new Etiqueta(nombre, descripcion);
		this.etiquetas.add(etiqueta);
	}

	public void agregarEdicion(Edicion edicion) { this.ediciones.add(edicion); }
}