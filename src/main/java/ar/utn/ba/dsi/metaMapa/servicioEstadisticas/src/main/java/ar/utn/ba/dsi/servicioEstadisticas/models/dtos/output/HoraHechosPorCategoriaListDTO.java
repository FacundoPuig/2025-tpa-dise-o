package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// Respuesta de la Métrica 4
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraHechosPorCategoriaListDTO {
  private String categoria; // Categoría consultada [cite: 457]
  private List<HoraHechosData> distribucion; // Distribución de conteos para cada hora del día [cite: 459]
}
