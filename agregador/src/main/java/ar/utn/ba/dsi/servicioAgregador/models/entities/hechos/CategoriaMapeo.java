package ar.utn.ba.dsi.servicioAgregador.models.entities.hechos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaMapeo {
	@JsonProperty("nombre_normalizado")
	private String nombreNormalizado;

	private List<String> sinonimos;
}
