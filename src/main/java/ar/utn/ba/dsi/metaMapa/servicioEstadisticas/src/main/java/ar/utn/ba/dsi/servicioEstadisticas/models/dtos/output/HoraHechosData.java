package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Representa un par (Hora del día y su conteo). (Métrica 4)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HoraHechosData {
  private Integer hora; // Valor de 0 a 23 [cite: 423]
  private Long cantidadHechos;
}
