package ar.utn.ba.dsi.servicioAgregador.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoraHechosPorCategoriaDTO {
  private int hora;
  private String categoria;
  private long cantidadHechos;
}
