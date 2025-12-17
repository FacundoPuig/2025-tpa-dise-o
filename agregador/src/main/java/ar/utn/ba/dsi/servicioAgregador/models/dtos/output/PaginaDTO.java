package ar.utn.ba.dsi.servicioAgregador.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginaDTO<T> {
	private List<T> contenido;
	private int paginaActual;
	private int tamanioPagina;
	private long totalElementos;
	private int totalPaginas;
	private boolean esUltima;
}