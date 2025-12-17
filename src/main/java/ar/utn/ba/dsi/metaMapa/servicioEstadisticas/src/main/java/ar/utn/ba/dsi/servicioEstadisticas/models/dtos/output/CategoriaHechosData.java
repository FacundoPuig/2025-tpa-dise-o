package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Representa un par (Categoría y su conteo). (Métrica 2)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaHechosData {
  private String categoria;
  private Long cantidadHechos;
}
