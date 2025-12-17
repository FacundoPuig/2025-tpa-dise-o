package ar.utn.ba.dsi.servicioUsuarios.models.dtos.input;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VisualizadorInputDTO {
		private String nombre;
		private String apellido;
		private LocalDate fechaDeNacimiento;
		private String email;
		private String contrasenia;
		private Boolean admin;
}