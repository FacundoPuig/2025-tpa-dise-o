package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.List;

// Respuesta de la Métrica 3
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinciaHechosPorCategoriaListDTO {
  private String categoria; // Categoría consultada [cite: 449]
  private List<ProvinciaHechosData> distribucion; // Lista de distribución de hechos por provincia para esa categoría [cite: 450]
}
