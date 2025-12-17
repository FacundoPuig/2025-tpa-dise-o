package ar.utn.ba.dsi.fuenteProxy.models.dtos.input;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HechoProxyInputDTO {
	private int id;// Formato de entrada para crear un Hecho
	private String titulo;
	private String descripcion;
	private String categoria;
	private Double latitud;
	private Double longitud;
	private LocalDateTime fecha_hecho;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
	//private Long idVisualizador;  // ID del visualizador que crea el hecho, se puede usar para saber quién lo creó
}






