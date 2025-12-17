package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// SolicitudSpamDTO se convierte en SolicitudSpamRatioDTO para el frontend (Métrica 5)
public class SolicitudSpamDTO {
  private Long cantidadSpam;
  private Long totalSolicitudes;
}
