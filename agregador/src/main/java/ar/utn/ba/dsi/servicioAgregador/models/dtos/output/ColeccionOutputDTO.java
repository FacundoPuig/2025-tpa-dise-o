package ar.utn.ba.dsi.servicioAgregador.models.dtos.output;

import lombok.Data;
import java.util.List;

@Data
public class ColeccionOutputDTO {
  private String titulo;
  private String descripcion;
  private String handleID;
  private String algoritmoConsenso;
  private List<String> fuentes; // Para mostrar nombres de fuentes
  private List<String> criterios; // Para mostrar resumen de criterios
}