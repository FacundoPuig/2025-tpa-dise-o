package ar.utn.ba.dsi.fuenteDinamica.models.dtos.input;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EdicionInputDTO {
  private String tituloPropuesto;
  private String descripcionPropuesta;
  private String categoriaPropuesta;
  private Double latitudPropuesta;
  private Double longitudPropuesta;
  private LocalDateTime fechaAcontecimientoPropuesta;
  private String contenidoMultimediaPropuesto;
  private String detalle;
}