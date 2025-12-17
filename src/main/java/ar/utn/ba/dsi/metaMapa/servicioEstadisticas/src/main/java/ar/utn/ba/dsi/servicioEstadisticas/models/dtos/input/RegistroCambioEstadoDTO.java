package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class RegistroCambioEstadoDTO {
  private Long Id;
  private String estado;
  private LocalDateTime fechaModificacion = LocalDateTime.now();

  @JsonProperty("idSolicitud")
  private long nroSolicitudEliminacion;

  @JsonProperty("descripcion")
  private String motivoRechazo;
}
