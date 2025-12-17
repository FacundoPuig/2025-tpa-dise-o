package ar.utn.ba.dsi.servicioAgregador.models.dtos.input;

import lombok.Data;
import java.util.List;

@Data
public class ColeccionEditarInputDTO {
  private String titulo;
  private String descripcion;
  private String visualizadorID;

  // Para editar fuentes
  private List<String> fuentes;

  // Para cambiar el algoritmo de consenso
  private String algoritmoConsenso;

  // Para criterios de pertenencia
  private List<String> criteriosPertenenciaNombres;
  private List<String> criteriosPertenenciaValores;
}
