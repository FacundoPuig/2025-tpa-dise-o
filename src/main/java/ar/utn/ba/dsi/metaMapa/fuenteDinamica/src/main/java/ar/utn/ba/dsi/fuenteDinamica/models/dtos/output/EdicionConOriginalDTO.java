package ar.utn.ba.dsi.fuenteDinamica.models.dtos.output;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoEdicion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EdicionConOriginalDTO {
	private long id;
	private String tituloPropuesto;
	private String descripcionPropuesta;
	private long categoriaPropuestaId;
	private String categoriaPropuestaNombre;
	private Double latitudPropuesta;
	private Double longitudPropuesta;
	private LocalDateTime fechaAcontecimientoPropuesta;
	private String contenidoMultimediaPropuesto;
	private String visualizadorEditor;
	private String detalle;
	private LocalDateTime fechaEdicion;
	private EstadoEdicion estado;

	private HechoOriginalDTO hechoOriginal; // <-- incluimos los datos originales
}
