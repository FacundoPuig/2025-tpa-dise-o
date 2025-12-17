package ar.utn.ba.dsi.fuenteDinamica.models.dtos.output;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoEdicion;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EdicionOutputDTO {
  private long id;
  private long idHechoOriginal;
  private String tituloPropuesto;
  private String descripcionPropuesta;
  private Long categoriaPropuestaId;
  private String categoriaPropuestaNombre;
  private Double latitudPropuesta;
  private Double longitudPropuesta;
  private LocalDateTime fechaAcontecimientoPropuesta;
  private String contenidoMultimediaPropuesto;
  private String visualizadorEditor;
  private LocalDate fechaEdicion;
  private EstadoEdicion estado;
  private String detalle;
}