package ar.utn.ba.dsi.fuenteDinamica.models.dtos.input;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Categoria;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HechoDinamicaInputDTO {  // Formato de entrada para crear un Hecho
	private String titulo;
	private String descripcion;
	private String categoria;
	private Double latitud;
	private Double longitud;
	private LocalDateTime fechaAcontecimiento;
	private String contenidoMultimedia;
	private String idVisualizador;  // ID del visualizador que crea el hecho, se puede usar para saber quién lo creó
}
