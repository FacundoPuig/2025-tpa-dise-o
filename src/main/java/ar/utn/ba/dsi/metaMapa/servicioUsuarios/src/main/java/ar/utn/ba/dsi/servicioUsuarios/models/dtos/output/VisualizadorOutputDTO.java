package ar.utn.ba.dsi.servicioUsuarios.models.dtos.output;

import ar.utn.ba.dsi.servicioUsuarios.models.entities.RolUsuario;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VisualizadorOutputDTO {
  private String nombre;
  private String apellido;
  private LocalDate fechaDeNacimiento;
  private Integer edad;
  private RolUsuario rolUsuario;
  private String id;
}
