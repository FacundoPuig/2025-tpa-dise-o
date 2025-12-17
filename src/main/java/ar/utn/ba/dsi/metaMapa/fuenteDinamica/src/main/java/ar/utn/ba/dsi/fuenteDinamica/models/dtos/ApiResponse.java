package ar.utn.ba.dsi.fuenteDinamica.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	private int codigo;      // Ej: 200, 400, 500
	private String estado;   // Ej: "OK", "ERROR"
	private String mensaje;  // Ej: "Hecho creado correctamente"
	private T datos;         // El objeto real (puede ser null)

	// Constructor helper para respuestas exitosas
	public static <T> ApiResponse<T> success(T datos, String mensaje) {
		return new ApiResponse<>(200, "OK", mensaje, datos);
	}

	// Constructor helper para errores o mensajes simples
	public static <T> ApiResponse<T> error(int codigo, String mensaje) {
		return new ApiResponse<>(codigo, "ERROR", mensaje, null);
	}
}