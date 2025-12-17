package ar.utn.ba.dsi.servicioAgregador.models.dtos.input;

import lombok.Data;

@Data
public class SolicitudEliminacionAgregadorInputDTO {
  private String motivo;
  private Long id;
}
