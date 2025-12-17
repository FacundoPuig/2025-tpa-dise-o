package ar.utn.ba.dsi.servicioAgregador.controller;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.services.IColeccionService;
import ar.utn.ba.dsi.servicioAgregador.services.IHechoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AgregadorGraphQLController {

	@Autowired
	private IHechoService hechoService;

	@Autowired
	private IColeccionService coleccionService;

	@QueryMapping
	public List<HechoAgregadorOutputDTO> listarHechos(
			@Argument("categoria") String categoria,
			@Argument("titulo") String titulo
	) {
		return hechoService.conseguirTodosLosHechos().stream()
				.filter(h -> categoria == null || (h.getCategoria() != null && h.getCategoria().equalsIgnoreCase(categoria)))
				.filter(h -> titulo == null || (h.getTitulo() != null && h.getTitulo().toLowerCase().contains(titulo.toLowerCase())))
				.collect(Collectors.toList());
	}

	@QueryMapping
	public List<ColeccionOutputDTO> listarColecciones() {
		return coleccionService.buscarTodas();
	}

	@QueryMapping
	public ColeccionOutputDTO obtenerColeccion(@Argument("id") String id) {
		return coleccionService.buscarPorId(id, null);
	}

	@QueryMapping
	public HechoAgregadorOutputDTO obtenerHecho(@Argument("id") String id) {
		// Convertimos el ID de String a Long
		try {
			Long idLong = Long.parseLong(id);
			return hechoService.obtenerHechoPorId(idLong);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@QueryMapping
	public List<HechoAgregadorOutputDTO> listarHechosPorRangoFecha(
			@Argument("fechaInicio") String fechaInicio,
			@Argument("fechaFin") String fechaFin) {

		try {
			// Asumimos formato ISO estándar (ej: "2025-11-01T10:00:00")
			LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
			LocalDateTime fin = LocalDateTime.parse(fechaFin);

			return hechoService.conseguirTodosLosHechos().stream()
					.filter(h -> h.getFechaAcontecimiento() != null)
					.filter(h -> h.getFechaAcontecimiento().isAfter(inicio) && h.getFechaAcontecimiento().isBefore(fin))
					.collect(Collectors.toList());
		} catch (Exception e) {
			System.err.println("Error parseando fechas en GraphQL: " + e.getMessage());
			return List.of();
		}
	}

	@QueryMapping
	public Integer contarHechosPorCategoria(@Argument("categoria") String categoria) {
		return (int) hechoService.conseguirTodosLosHechos().stream()
				.filter(h -> h.getCategoria() != null && h.getCategoria().equalsIgnoreCase(categoria))
				.count();
	}

	@QueryMapping
	public List<String> listarCategoriasUnicas() {
		return hechoService.conseguirTodosLosHechos().stream()
				.map(HechoAgregadorOutputDTO::getCategoria)
				.distinct()
				.collect(Collectors.toList());
	}

	@QueryMapping
	public List<HechoAgregadorOutputDTO> listarHechosPorNombreOrigen(@Argument("nombreOrigen") String nombreOrigen) {
		return hechoService.conseguirTodosLosHechos().stream()
				.filter(h -> h.getNombreOrigen() != null && h.getNombreOrigen().toUpperCase().contains(nombreOrigen.toUpperCase()))
				.collect(Collectors.toList());
	}

	@SchemaMapping(typeName = "Coleccion", field = "hechos")
	public List<HechoAgregadorOutputDTO> obtenerHechosDeColeccion(ColeccionOutputDTO coleccion) {
		return coleccionService.devolverHechosDeColeccion(coleccion.getHandleID(), null);
	}
}