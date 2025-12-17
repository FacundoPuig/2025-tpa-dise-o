package ar.utn.ba.dsi.fuenteProxy.models.dtos.input;

import ar.utn.ba.dsi.fuenteProxy.models.dtos.external.HechosApiResponseDTO;
import lombok.Data;
import java.util.List;

@Data
public class ColeccionProxyInputDTO {
	private String titulo;
	private String descripcion;
	private List<HechosApiResponseDTO> hechos;

	//private List<Filtros> criteriosPertenencia;
	//private Fuente fuente;
}
