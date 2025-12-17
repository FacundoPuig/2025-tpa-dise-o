package ar.utn.ba.dsi.servicioUsuarios.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.UserRolesPermissionsDTO;
import java.time.LocalDate;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
	private String token;
	private UserRolesPermissionsDTO rolesPermisos;
	private Long id;
	private String nombre;
	private String apellido;
	private String email;
	private LocalDate fechaDeNacimiento;
}