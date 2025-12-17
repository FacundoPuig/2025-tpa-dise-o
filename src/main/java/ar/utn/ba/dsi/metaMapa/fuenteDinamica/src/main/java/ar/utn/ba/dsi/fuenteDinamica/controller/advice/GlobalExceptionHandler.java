package ar.utn.ba.dsi.fuenteDinamica.controller.advice;

import ar.utn.ba.dsi.fuenteDinamica.exceptions.ResourceNotFoundException;
import ar.utn.ba.dsi.fuenteDinamica.exceptions.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return new ResponseEntity<>(body, status);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex) {
		// Log the exception here for debugging purposes
		ex.printStackTrace();
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor.");
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<Object> handleMissingPart(MissingServletRequestPartException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Bad Request");
		body.put("message", "Falta la parte requerida: " + ex.getRequestPartName() + ". Asegurate de enviar el Content-Type correcto en Postman.");

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class) // Atrapamos el RuntimeException que lanzamos en el Controller
	public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
		if (ex.getCause() instanceof JsonProcessingException) {
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("timestamp", LocalDateTime.now());
			body.put("status", HttpStatus.BAD_REQUEST.value());
			body.put("error", "Invalid JSON");
			body.put("message", "El formato del JSON es inválido. Verificá la sintaxis.");
			return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
		}
		// Si es otro error, dejamos que pase o lo manejamos genérico
		return handleGenericException(ex);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.METHOD_NOT_ALLOWED.value()); // 405
		body.put("error", "Method Not Allowed");

		// Mensaje amigable: "El método GET no está soportado. Los soportados son: [PUT]"
		StringBuilder builder = new StringBuilder();
		builder.append("El método ");
		builder.append(ex.getMethod());
		builder.append(" no está soportado para esta ruta. ");

		if (ex.getSupportedHttpMethods() != null) {
			builder.append("Los métodos permitidos son: ");
			ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
		}

		body.put("message", builder.toString());

		return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Object> handleNotFound(NoResourceFoundException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.NOT_FOUND.value()); // 404
		body.put("error", "Not Found");
		body.put("message", "El endpoint solicitado no existe. Verificá la URL: " + ex.getResourcePath());

		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}
}