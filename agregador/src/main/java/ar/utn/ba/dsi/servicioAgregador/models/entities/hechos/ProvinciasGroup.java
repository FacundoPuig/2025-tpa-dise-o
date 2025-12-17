package ar.utn.ba.dsi.servicioAgregador.models.entities.hechos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinciasGroup {
	private int cantidad;
	private int total;
	private int inicio;
	private Map<String, Object> parametros;
	private List<Provincia> provincias;
}

