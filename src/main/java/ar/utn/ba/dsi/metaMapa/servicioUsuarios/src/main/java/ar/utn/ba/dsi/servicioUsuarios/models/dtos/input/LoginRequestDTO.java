package ar.utn.ba.dsi.servicioUsuarios.models.dtos.input;

import lombok.Data;

@Data
public class LoginRequestDTO {
	private String email;
	private String password;
}