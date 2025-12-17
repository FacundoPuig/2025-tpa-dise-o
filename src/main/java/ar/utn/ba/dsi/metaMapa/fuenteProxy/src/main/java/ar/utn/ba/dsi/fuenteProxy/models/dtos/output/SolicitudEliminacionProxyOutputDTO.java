package ar.utn.ba.dsi.fuenteProxy.models.dtos.output;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SolicitudEliminacionProxyOutputDTO {

  private Long nroDeSolicitud;
  private Long HechoID;
  private String tituloDelHechoAEliminar;
  private String motivo;
  LocalDateTime fechaCreacionSolicitud;

}
