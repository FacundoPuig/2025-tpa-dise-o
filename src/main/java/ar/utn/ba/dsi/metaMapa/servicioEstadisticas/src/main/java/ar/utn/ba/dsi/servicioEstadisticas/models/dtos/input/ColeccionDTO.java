package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ColeccionDTO {
  private String titulo;
  private String descripcion;
  private String handleID;
}
