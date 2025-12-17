package ar.utn.ba.dsi.fuenteDinamica.models.dtos.output;

import lombok.Data;

@Data
public class HechoOriginalDTO {
	private Long id;
	private String titulo;
	private String descripcion;
	private String categoriaNombre;
}
