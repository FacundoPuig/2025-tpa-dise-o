package ar.utn.ba.dsi.servicioAgregador.models.dtos.input;

import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso.Algoritmos;
import lombok.Data;

@Data
public class ColeccionInputManual_DTO {
	private String titulo;
	private String descripcion;
	private String visualizadorID;
	private String algoritmoConsenso;
	private String fuenteNombre; // DINAMICA / ESTATICA
	private String fuenteUrl;

	//private List<Filtros> criteriosPertenencia;
	//private Fuente fuente;
}
