package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Representa un par (Provincia y su conteo) en una distribución geográfica. (Métrica 1 y 3)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinciaHechosData {
  private String provincia;
  private Long cantidadHechos;
}
