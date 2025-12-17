package ar.utn.ba.dsi.servicioAgregador.models.dtos.input;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Categoria;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HechoAgregadorInputDTO {  // Formato de entrada para crear un Hecho
	private Long id;
	private String titulo;
	private String descripcion;
	private String categoria;
	private Double latitud;
	private Double longitud;
	private LocalDateTime fechaAcontecimiento;
	private String contenidoMultimedia;
	private String provincia;
	private Long idVisualizador;  // ID del visualizador que crea el hecho, se puede usar para saber quién lo creó
}
