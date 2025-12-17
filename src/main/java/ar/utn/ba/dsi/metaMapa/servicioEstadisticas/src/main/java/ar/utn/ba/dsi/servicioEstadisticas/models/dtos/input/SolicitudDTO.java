package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Data
public class SolicitudDTO {
  private Long nroDeSolicitud;
  private String tituloHecho;
  private String tituloDelHechoAEliminar;
  private String estado;
  private String motivo;
  private LocalDateTime fechaCreacionSolicitud;

}
