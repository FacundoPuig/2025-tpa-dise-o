package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

// Respuesta de la Métrica 2
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaReportadaListDTO {
  private List<CategoriaHechosData> distribucion; // Lista de distribución de hechos por categoría [cite: 440]
}
