package ar.utn.ba.dsi.servicioAgregador.models.dtos.output;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HechoAgregadorOutputDTO { // Formato de salida para un Hecho
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private double latitud;
  private double longitud;
  private String provincia;
  private LocalDateTime fechaAcontecimiento;
  private LocalDate fechaCarga;
  private String contenidoMultimedia;
  private String nombreOrigen;
  private String provieneDeOrigen;
  private List<String> nombreEtiquetas;
  private String handleIdColeccion;
  //private String estado;
}
