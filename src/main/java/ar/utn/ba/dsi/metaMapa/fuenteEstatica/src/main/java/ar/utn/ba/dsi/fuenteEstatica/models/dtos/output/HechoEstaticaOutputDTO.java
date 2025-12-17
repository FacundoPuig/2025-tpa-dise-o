package ar.utn.ba.dsi.fuenteEstatica.models.dtos.output;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HechoEstaticaOutputDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private LocalDateTime fechaAcontecimiento;
  private LocalDate fechaCarga;
  private String contenidoMultimedia;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String provincia; // Descomentar si tu entidad Ubicacion tiene provincia
  private List<String> etiquetas;
  private String nombreOrigen;
  private String provieneDeOrigen;
  private String estado;
}