package ar.utn.ba.dsi.fuenteDinamica.models.dtos.output;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoRevision;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SolicitudOutputDTO {
	private long id;
	private String motivo;
	private LocalDate fechaSolicitud;
	private String solicitanteId;
	private EstadoRevision estado;
	private long idHecho;
	private String tituloHecho;
}