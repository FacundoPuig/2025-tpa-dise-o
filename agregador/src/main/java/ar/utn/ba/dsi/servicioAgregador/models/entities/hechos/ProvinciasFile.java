package ar.utn.ba.dsi.servicioAgregador.models.entities.hechos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinciasFile {
	private List<ProvinciasGroup> provincias;
}

