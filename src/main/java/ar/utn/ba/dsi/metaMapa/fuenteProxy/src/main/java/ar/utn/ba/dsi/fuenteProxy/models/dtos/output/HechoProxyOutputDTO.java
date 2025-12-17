package ar.utn.ba.dsi.fuenteProxy.models.dtos.output;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HechoProxyOutputDTO { // Formato de salida para un Hecho
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
}







