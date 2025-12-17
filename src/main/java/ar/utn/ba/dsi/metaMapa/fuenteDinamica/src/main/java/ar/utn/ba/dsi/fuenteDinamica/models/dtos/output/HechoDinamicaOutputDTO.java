package ar.utn.ba.dsi.fuenteDinamica.models.dtos.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HechoDinamicaOutputDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fechaAcontecimiento;
  private LocalDate fechaCarga;
  private String contenidoMultimedia;
  private String nombreOrigen;
  private String provieneDeOrigen;
  private List<String> etiquetas;
  private String estado;
  private String sugerenciaAdmin;
  private boolean tieneEdicionPendiente;
}
