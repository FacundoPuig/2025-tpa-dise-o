package ar.utn.ba.dsi.servicioAgregador.models.dtos.output;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.Estados;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SolicitudEliminacionAgregadorOutputDTO {

  private Long nroDeSolicitud;
  private String nombreHecho;
  private Long idDelHecho;
  private String estado;
  private String motivo;
  private LocalDateTime fechaCreacionSolicitud;


}
