package ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class HechoDTO {
	private String titulo;
	private String descripcion;
	private String categoria;
	private double latitud;
	private double longitud;
	private String provincia;
	private LocalDateTime fechaAcontecimiento;
	private LocalDate fechaCarga;

	@JsonProperty("handleIdColeccion")
	private String handleID;
}
