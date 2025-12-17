package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// Respuesta de la Métrica 1
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinciaHechosPorColeccionListDTO {
  private String handleID; // ID de la colección consultada [cite: 431]
  private List<ProvinciaHechosData> distribucion; // Lista de distribución de hechos por provincia [cite: 433]
}
