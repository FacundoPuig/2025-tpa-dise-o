package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones;

import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso.Algoritmos;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros.Filtros;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros.FiltrosConverter;
import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.fuentes.Fuente;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.intermedia.ColeccionHecho;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="coleccion")
public class Coleccion {

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="handle_id", length = 255)
	private String handleID;

	@Column(name="titulo", length = 200)
	private String titulo;

	@Column(name="descripcion", length = 200)
	private String descripcion;

	// Reemplazamos @ManyToMany por @OneToMany apuntando a la intermedia
	@OneToMany(mappedBy = "coleccion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ColeccionHecho> coleccionHechos = new ArrayList<>();

	@Convert(converter = FiltrosConverter.class)
	@Column(columnDefinition = "TEXT")
	private List<Filtros> criteriosPertenencia = new ArrayList<>();

//	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JoinTable(
//			name = "coleccion_algoritmo",
//			joinColumns = @JoinColumn(name = "coleccion_id"),
//			inverseJoinColumns = @JoinColumn(name = "algoritmo_id")
//	)
	private Algoritmos algoritmoConsenso;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "coleccion_fuente",
			joinColumns = @JoinColumn(name = "coleccion_id"),
			inverseJoinColumns = @JoinColumn(name = "fuente_id")
	)
	private Set<Fuente> fuentes = new HashSet<>();

	public Coleccion(String titulo, String descripcion,String id, Algoritmos algoritmoConsenso) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.handleID = id;
		this.algoritmoConsenso = algoritmoConsenso;
	}

	public List<ColeccionHecho> aplicarFiltros(List<Filtros> filtros) {

		if(filtros.isEmpty())
			return this.coleccionHechos;
		return this.coleccionHechos.stream()
				.filter(coleccionHecho ->
						filtros.stream()
								.allMatch(criterio -> criterio.filtrar(coleccionHecho.getHecho()))
				)
				.collect(Collectors.toList());
	}

	// MÉTODO CLAVE: Para que el resto de tu código siga sintiendo que agrega Hechos
	// y no se preocupe por la tabla intermedia
	public void agregarHecho(List<Hecho> hechos) {
		// Creamos la instancia intermedia automáticamente aquí
		hechos.forEach(hecho -> this.coleccionHechos.add(new ColeccionHecho(this, hecho)));
	}

	// edita la lista de hechos de la coleccion
	public void agregarHechos(List<Hecho> nuevosHechos) {
		nuevosHechos.forEach(this::agregarOReemplazar);
		this.coleccionHechos = this.aplicarFiltros(this.criteriosPertenencia);
	}

	public void agregarFuente(Fuente nuevaFuente) {
		this.fuentes.add(nuevaFuente);
		this.coleccionHechos = this.aplicarFiltros(this.criteriosPertenencia);
	}

	public void quitarFuente(Fuente fuenteAEliminar) {
		this.fuentes.remove(fuenteAEliminar);
		this.coleccionHechos = this.aplicarFiltros(this.criteriosPertenencia);
	}


	//esto es para cuando se hace una edicion de hecho desde dinamica
	private void agregarOReemplazar(Hecho nuevoHecho) {
		Integer index = this.buscarIndicePorTituloYOrigen(nuevoHecho);
		if (index != -1) {
			coleccionHechos.set(index, new ColeccionHecho(this,nuevoHecho));
		} else { //esnuevo
			coleccionHechos.add(new ColeccionHecho(this,nuevoHecho));
		}
	}
	private Integer buscarIndicePorTituloYOrigen(Hecho hecho) {
		//String tituloNuevo = hecho.getTitulo();
		String nombreOrigenNuevo = hecho.getOrigen() != null ? hecho.getOrigen().getNombre() : null;

		for (Integer i = 0; i < coleccionHechos.size(); i++) {
			Hecho existente = coleccionHechos.get(i).getHecho();

			//String tituloExistente = existente.getTitulo();
			String nombreOrigenExistente = existente.getOrigen() != null ? existente.getOrigen().getNombre() : null;

			// Un hecho solo es un reemplazo si el título Y el origen son iguales
			if (existente.esIgual(hecho) && Objects.equals(nombreOrigenExistente, nombreOrigenNuevo)) {
				System.out.println("Reemplazando hecho en índice: " + i); // Cambio para debug
				return i;
			}
		}
		return -1;
	}

	public List<Hecho> getHechos() {
		return this.coleccionHechos.stream()
				.map(ColeccionHecho::getHecho)
				.collect(Collectors.toList());
	}

}
