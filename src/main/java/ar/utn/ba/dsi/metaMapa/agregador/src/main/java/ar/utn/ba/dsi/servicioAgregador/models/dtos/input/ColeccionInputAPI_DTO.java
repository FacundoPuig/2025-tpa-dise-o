package ar.utn.ba.dsi.servicioAgregador.models.dtos.input;

import lombok.Data;
import java.util.List;

@Data
public class ColeccionInputAPI_DTO {
	private String titulo;
	private String descripcion;
	private List<HechosApiResponseDTO> hechos;

	//private List<Filtros> criteriosPertenencia;
	//private Fuente fuente;
}
