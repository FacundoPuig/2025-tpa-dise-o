package ar.utn.ba.dsi.servicioAgregador.models.dtos;

import lombok.Data;

@Data
public class ProvinciaMasHechosPorColeccionDTO {
  private String provincia;
  private String handleID;
  private long cantidadHechos;
}
