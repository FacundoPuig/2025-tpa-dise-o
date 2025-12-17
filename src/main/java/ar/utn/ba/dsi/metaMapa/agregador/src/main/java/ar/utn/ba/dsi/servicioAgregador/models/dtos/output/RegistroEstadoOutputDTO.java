package ar.utn.ba.dsi.servicioAgregador.models.dtos.output;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistroEstadoOutputDTO {
  private Long idRegistroEstado;
  private Long idSolicitud;
  private String estado;
  private String modificadoPor;
  private String descripcion;
  private LocalDateTime fechaModificacion;
}
