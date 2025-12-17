package ar.utn.ba.dsi.fuenteEstatica.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Este método actuará como un "atrapa-todo".
	 * Si cualquier controlador lanza una excepción que no sea manejada específicamente,
	 * este método la capturará y devolverá una respuesta HTTP 500 estandarizada.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		// Imprimimos el error en la consola del servidor para poder depurarlo.
		ex.printStackTrace();

		// Devolvemos una respuesta limpia al cliente.
		return new ResponseEntity<>("Ocurrió un error interno en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Maneja RuntimeException y JpaSystemException (comunes en errores de negocio/DB)
	 * y devuelve un JSON con el mensaje de error para que el frontend lo pueda mostrar.
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
		ex.printStackTrace(); // Para logs del servidor

		// Intentamos extraer un mensaje más limpio
		String message = ex.getMessage();

		// Si es un error de Spring/Hibernate, intenta ser un poco más descriptivo.
		if (ex.getClass().getName().contains("JpaSystemException")) {
			message = "Error en la base de datos: " + (ex.getCause() != null ? ex.getCause().getMessage() : message);
		} else if (ex.getCause() != null && ex.getCause().getMessage() != null) {
			message = ex.getCause().getMessage();
		}

		// Mensaje de fallback
		if (message == null || message.isEmpty()) {
			message = "Ocurrió un error inesperado al procesar el archivo.";
		}


		return new ResponseEntity<>(
				Map.of("message", message, "errorType", ex.getClass().getSimpleName()),
				HttpStatus.INTERNAL_SERVER_ERROR
		);
	}
}