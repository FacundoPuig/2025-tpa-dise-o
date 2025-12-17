package ar.utn.ba.dsi.servicioAgregador.models.entities.hechos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Provincia {
	private String id;
	private String nombre;
	private String nombre_completo;
	private String fuente;
	private String categoria;
	private Centroide centroide;
	private String iso_id;
	private String iso_nombre;


	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Centroide {
		private double lon;
		private double lat;
	}
}
