package ar.utn.ba.dsi.fuenteEstatica.models.dtos.input;

import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Categoria;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HechoEstaticaInputDTO {  // Formato de entrada para crear un Hecho
	private String titulo;
	private String descripcion;
	private Categoria categoria;
	private Double latitud;
	private Double longitud;
	private LocalDateTime fechaAcontecimiento;
	private String contenidoMultimedia;
	private Long idVisualizador;  // ID del visualizador que crea el hecho, se puede usar para saber quién lo creó
}
