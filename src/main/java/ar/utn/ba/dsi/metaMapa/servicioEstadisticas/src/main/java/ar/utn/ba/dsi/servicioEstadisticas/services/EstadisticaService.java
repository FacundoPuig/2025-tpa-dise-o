package ar.utn.ba.dsi.servicioEstadisticas.services;

import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.CategoriaHechosData;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.CategoriaReportadaListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.HoraHechosData;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.HoraHechosPorCategoriaListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.ProvinciaHechosData;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.ProvinciaHechosPorCategoriaListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.ProvinciaHechosPorColeccionListDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.output.SolicitudSpamDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaDeSpam;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorHoraYCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorProvincia;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorProvinciaYCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaDeSpamRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorCategoriaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorHoraYCategoriaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorProvinciaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorProvinciaYCategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadisticaService {
	@Autowired
	private IEstadisticaPorProvinciaRepository estadisticaPorProvinciaRepository;
	@Autowired
	private IEstadisticaPorCategoriaRepository estadisticaPorCategoriaRepository;
	@Autowired
	private IEstadisticaPorProvinciaYCategoriaRepository estadisticaPorProvinciaYCategoriaRepository;
	@Autowired
	private IEstadisticaPorHoraYCategoriaRepository estadisticaPorHoraYCategoriaRepository;
	@Autowired
	private IEstadisticaDeSpamRepository estadisticaDeSpamRepository;

	// --- MÉTODOS PARA DASHBOARD (Devuelven List DTO) ---

	// 1) Distribución de provincias por colección
	public ProvinciaHechosPorColeccionListDTO getDistribucionProvinciasPorColeccion(String handleId) {
		EstadisticaPorProvincia estadistica = estadisticaPorProvinciaRepository
				.findByColeccionHandleAndEsUltimaTrue(handleId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se encontró la estadística activa para la colección con handleId: " + handleId));

		// Mapea la lista de entidades hijas (EstadisticaProvinciaData) a la lista de DTOs públicos (ProvinciaHechosData)
		List<ProvinciaHechosData> distribucion = estadistica.getDistribucionData().stream()
				.map(data -> new ProvinciaHechosData(data.getProvincia(), data.getCantidadHechos()))
				.collect(Collectors.toList());

		return new ProvinciaHechosPorColeccionListDTO(estadistica.getColeccionHandle(), distribucion);
	}

	// 2) Distribución de categorías global
	public CategoriaReportadaListDTO getDistribucionCategorias() {
		EstadisticaPorCategoria estadistica = estadisticaPorCategoriaRepository.findAllByEsUltimaTrue().stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Estadística total de categoría más reportada no encontrada. El proceso de cálculo falló"));

		// Mapea la lista de entidades hijas (EstadisticaCategoriaData) a la lista de DTOs públicos (CategoriaHechosData)
		List<CategoriaHechosData> distribucion = estadistica.getDistribucionData().stream()
				.map(data -> new CategoriaHechosData(data.getCategoria(), data.getCantidadHechos()))
				.collect(Collectors.toList());

		return new CategoriaReportadaListDTO(distribucion);
	}


	// 3) Distribución de provincias por categoría
	public ProvinciaHechosPorCategoriaListDTO getDistribucionProvinciasPorCategoria(String categoria) {
		EstadisticaPorProvinciaYCategoria estadistica = estadisticaPorProvinciaYCategoriaRepository
				.findByCategoriaAndEsUltimaTrue(categoria)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se encontró la estadística activa para la categoría: " + categoria));

		// Mapea la lista de entidades hijas (EstadisticaProvinciaYCategoriaData) a la lista de DTOs públicos (ProvinciaHechosData)
		List<ProvinciaHechosData> distribucion = estadistica.getDistribucionData().stream()
				.map(data -> new ProvinciaHechosData(data.getProvincia(), data.getCantidadHechos()))
				.collect(Collectors.toList());

		return new ProvinciaHechosPorCategoriaListDTO(estadistica.getCategoria(), distribucion);
	}

	// 4) Distribución de horas por categoría
	public HoraHechosPorCategoriaListDTO getDistribucionHorasPorCategoria(String categoria) {

		EstadisticaPorHoraYCategoria estadistica = estadisticaPorHoraYCategoriaRepository
				.findByCategoriaAndEsUltimaTrue(categoria)
				.orElseThrow(()-> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se encontró la estadística activa para la categoría: " + categoria));

		// Mapea la lista de entidades hijas (EstadisticaHoraData) a la lista de DTOs públicos (HoraHechosData)
		List<HoraHechosData> distribucion = estadistica.getDistribucionData().stream()
				.map(data -> new HoraHechosData(data.getHoraDelDia(), data.getCantidadHechos()))
				.collect(Collectors.toList());

		return new HoraHechosPorCategoriaListDTO(estadistica.getCategoria(), distribucion);
	}


	// 5) Ratio de solicitudes de eliminación spam
	public SolicitudSpamDTO getSolicitudesSpamRatio() { // Usando SolicitudSpamDTO como SolicitudSpamRatioDTO
		EstadisticaDeSpam estadisticaDeSpam = estadisticaDeSpamRepository.findAllByEsUltimaTrue().stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Estadística total de solicitudes spam no encontrada. El proceso de cálculo falló"));




		SolicitudSpamDTO solicitudSpam = new SolicitudSpamDTO();
		solicitudSpam.setCantidadSpam(estadisticaDeSpam.getCantidadSpam());
		solicitudSpam.setTotalSolicitudes(estadisticaDeSpam.getTotalSolicitudes()); // Campo nuevo

		System.out.println("Cantidad Spam: " + solicitudSpam.getCantidadSpam());
		System.out.println("Total Solicitudes: " + solicitudSpam.getTotalSolicitudes());

		System.out.println(solicitudSpam);

		return solicitudSpam;
	}


	/*// --- MÉTODOS ANTIGUOS (Mantener para compatibilidad / CSV Export) ---

	// 1)
	public ProvinciaMasHechosPorColeccionDTO getProvinciaMasHechos(String handleId) {
		EstadisticaPorProvincia estadisticaPorProvincia = estadisticaPorProvinciaRepository
				.findByColeccionHandleAndEsUltimaTrue(handleId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND, // Devuelva un 404
						"No se encontró la estadística activa para la colección con handleId: " + handleId));

		ProvinciaMasHechosPorColeccionDTO provinciaConMasHechos = new ProvinciaMasHechosPorColeccionDTO();
		provinciaConMasHechos.setProvincia(estadisticaPorProvincia.getProvincia());
		provinciaConMasHechos.setHandleID(estadisticaPorProvincia.getColeccionHandle());
		provinciaConMasHechos.setCantidadHechos(estadisticaPorProvincia.getCantidadHechos());
		return provinciaConMasHechos;
	}

	//2)
	public CategoriaMasReportadaDTO getCategoriaConMasHechos() {
		EstadisticaPorCategoria estadisticaPorCategoria = estadisticaPorCategoriaRepository.findAllByEsUltimaTrue().stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, //da error 500, xq la estadistica deberia existir
						"Estadística total de categoría más reportada no encontrada. El proceso de cálculo falló"));

		CategoriaMasReportadaDTO categoriaMasReportada = new CategoriaMasReportadaDTO();
		categoriaMasReportada.setCategoria(estadisticaPorCategoria.getCategoria());
		categoriaMasReportada.setCantidadHechos(estadisticaPorCategoria.getCantidadHechos());

		return categoriaMasReportada;
	}


	//3)
	public ProvinciaHechosPorCategoriaDTO getProvinciaMasHechosPorCategoria(String categoria) {
		EstadisticaPorProvinciaYCategoria estadisticaPorProvinciaYCategoria = estadisticaPorProvinciaYCategoriaRepository
				.findByCategoriaAndEsUltimaTrue(categoria)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se encontró la estadística activa para la categoría: " + categoria));

		ProvinciaHechosPorCategoriaDTO provinciaHechosPorCategoriaDTO = new ProvinciaHechosPorCategoriaDTO();
		provinciaHechosPorCategoriaDTO.setProvincia(estadisticaPorProvinciaYCategoria.getProvincia());
		provinciaHechosPorCategoriaDTO.setCantidadHechos(estadisticaPorProvinciaYCategoria.getCantidadHechos());
		provinciaHechosPorCategoriaDTO.setCategoria(estadisticaPorProvinciaYCategoria.getCategoria());

		return provinciaHechosPorCategoriaDTO;
	}

	// 4)
	public HoraHechosPorCategoriaDTO getHechosPorHoraSegunCategoria(String categoria) {

		EstadisticaPorHoraYCategoria estadistica = estadisticaPorHoraYCategoriaRepository
				.findByCategoriaAndEsUltimaTrue(categoria)
				.orElseThrow(()-> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se encontró la estadística activa para la categoría: " + categoria));

		HoraHechosPorCategoriaDTO horaHechosPorCategoriaDTO = new HoraHechosPorCategoriaDTO();
		horaHechosPorCategoriaDTO.setHora(estadistica.getHoraDelDia());
		horaHechosPorCategoriaDTO.setCategoria(estadistica.getCategoria());
		horaHechosPorCategoriaDTO.setCantidadHechos(estadistica.getCantidadHechos());
		return horaHechosPorCategoriaDTO;
	}

	//5)
	public SolicitudSpamDTO getCantidadSpam() {
		EstadisticaDeSpam estadisticaDeSpam = estadisticaDeSpamRepository.findAllByEsUltimaTrue().stream()
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Estadística total de solicitudes spam no encontrada. El proceso de cálculo falló"));

		SolicitudSpamDTO solicitudSpam = new SolicitudSpamDTO();
		solicitudSpam.setCantidadSpam(estadisticaDeSpam.getCantidadSpam());
		return solicitudSpam;
	}
}*/


}
