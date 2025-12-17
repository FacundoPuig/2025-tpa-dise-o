package ar.utn.ba.dsi.servicioAgregador.models.dtos;

import lombok.Data;

@Data
public class CategoriaMasReportadaDTO {
  private String categoria;
  private long cantidadHechos;
}
