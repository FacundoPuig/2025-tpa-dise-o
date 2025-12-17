package ar.utn.ba.dsi.servicioAgregador.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	private int codigo;
	private String estado;
	private String mensaje;
	private T datos;
}