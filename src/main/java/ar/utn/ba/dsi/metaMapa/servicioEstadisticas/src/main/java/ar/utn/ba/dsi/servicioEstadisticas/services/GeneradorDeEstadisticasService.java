package ar.utn.ba.dsi.servicioEstadisticas.services;

import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input.ColeccionDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input.HechoDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.dtos.input.RegistroCambioEstadoDTO;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaDeSpam;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorHoraYCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorProvincia;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorProvinciaYCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaProvinciaData;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaCategoriaData;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaProvinciaYCategoriaData;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaHoraData;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaDeSpamRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorCategoriaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorHoraYCategoriaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorProvinciaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorProvinciaYCategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeneradorDeEstadisticasService {

  @Autowired
  private AgregadorClient agregadorClient;
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

  // TODO: AUMENTAR LA CANTIDAD DE HILOS PARA Q EJECUTE EN PARALELO EN =! HILOS

	// De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?
	//@Scheduled(cron = "0 0 * * * *")
	@Scheduled(fixedRate =  120000)
	@Transactional
	public void calcularEstadisticasPorProvincia() {

		List<ColeccionDTO> colecciones = agregadorClient.getColecciones();
		List<HechoDTO> hechos = agregadorClient.getHechos(); //aca tengo los hechos q conocen el handleID de la coleccion a la q pertencen

		this.marcarProvinciaComoAntiguas();

		for (ColeccionDTO coleccion : colecciones) {

			// agrupa la coleccion con los hechos q la componen
			List<HechoDTO> hechosDeColeccion = hechos.stream()
					.filter(h -> coleccion.getHandleID().equals(h.getHandleID()))
					.toList();

			// a los hechos de una coleccion, las agrupa <Provincia, cantidadDeHechos>
			Map<String, Long> conteo = hechosDeColeccion.stream()
					.collect(Collectors.groupingBy(HechoDTO::getProvincia, Collectors.counting()));

			// de lo aneterior compara los valores y se queda con el max
			Map.Entry<String, Long> max = conteo.entrySet().stream()
					.max(Map.Entry.comparingByValue())
					.orElse(Map.entry("N/A", 0L));

			//crea la entidad padre
			EstadisticaPorProvincia estadisticaPorProvincia = new EstadisticaPorProvincia();
			estadisticaPorProvincia.setColeccionHandle(coleccion.getHandleID());
			estadisticaPorProvincia.setColeccionTitulo(coleccion.getTitulo());
			estadisticaPorProvincia.setProvincia(max.getKey());
			estadisticaPorProvincia.setCantidadHechos(max.getValue());
			estadisticaPorProvincia.setFechaGeneracion(LocalDateTime.now());
			estadisticaPorProvincia.setEsUltima(true);

			//2. Crear las entidades hijas de distribución (Mapeo)
			List<EstadisticaProvinciaData> distribucionData = conteo.entrySet().stream()
					.map(e -> {
						EstadisticaProvinciaData data = new EstadisticaProvinciaData();
						data.setProvincia(e.getKey());
						data.setCantidadHechos(e.getValue());
						data.setEstadisticaPorProvincia(estadisticaPorProvincia);
						return data;
					}).toList();

			// 3. Asignar la lista al padre
			estadisticaPorProvincia.setDistribucionData(distribucionData);

			estadisticaPorProvinciaRepository.save(estadisticaPorProvincia);
		}
	}

	// 2) ¿Cuál es la categoría con mayor cantidad de hechos reportados?
	//@Scheduled(cron = "0 0 * * * *")
	@Scheduled(fixedRate =  120000)
	@Transactional
	public void calcularEstadisticasPorCategoria() {

		List<HechoDTO> hechos = agregadorClient.getHechos();

		this.marcarCategoriaComoAntiguas();

		// Map<Categoria, cantDeHechos>
		Map<String, Long> conteoPorCategoria = hechos.stream()
				.collect(Collectors.groupingBy(h -> h.getCategoria(), Collectors.counting()));

		// encuentra la entrada (categoría y conteo) con el valor más alto
		conteoPorCategoria.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.ifPresent(max -> {
					// Crea la clase padre
					EstadisticaPorCategoria estadisticaPorCategoria = new EstadisticaPorCategoria();
					estadisticaPorCategoria.setCategoria(max.getKey());
					estadisticaPorCategoria.setCantidadHechos(max.getValue());
					estadisticaPorCategoria.setFechaGeneracion(LocalDateTime.now());
					estadisticaPorCategoria.setEsUltima(true);

					//Crea clase hija de dsitribucion
					List<EstadisticaCategoriaData> distribucionData = conteoPorCategoria.entrySet().stream()
							.map(e -> {
								EstadisticaCategoriaData data = new EstadisticaCategoriaData();
								data.setCategoria(e.getKey());
								data.setCantidadHechos(e.getValue());
								data.setEstadisticaPorCategoria(estadisticaPorCategoria); // Asignar el padre
								return data;
							}).toList();

					//lo carga al padre
					estadisticaPorCategoria.setDistribucionData(distribucionData);
					estadisticaPorCategoriaRepository.save(estadisticaPorCategoria);
				});
	}


	// 3) ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
	//@Scheduled(cron = "0 0 * * * *")
	@Scheduled(fixedRate =  120000)
	@Transactional
	public void calcularEstadisticaPorProvinciaYCategoria() {

		List<HechoDTO> hechos = agregadorClient.getHechos();

		this.marcarProvinciaYCategoriaComoAntiguas();

		List<String> categoriasUnicas = hechos.stream()
				.map(HechoDTO::getCategoria)
				.distinct()
				.toList();


		for (String categoriaDeInteres : categoriasUnicas) {

			//filtra los hechos q son de una misma categoria y las agrupa en <Provincia, cantidad>
			Map<String, Long> conteoPorProvincia = hechos.stream()
					.filter(h -> h.getCategoria().equals(categoriaDeInteres))
					.collect(Collectors.groupingBy(h -> h.getProvincia(), Collectors.counting()));

			// para la categoria en cuestion, toma la provicnia q tiene mayor cant de hechos
			conteoPorProvincia.entrySet().stream()
					.max(Map.Entry.comparingByValue())
					.ifPresent(max -> {

						//Crea y carga el padre
						EstadisticaPorProvinciaYCategoria estadisticaPorProvinciaYCategoria = new EstadisticaPorProvinciaYCategoria();
						estadisticaPorProvinciaYCategoria.setCategoria(categoriaDeInteres);
						estadisticaPorProvinciaYCategoria.setProvincia(max.getKey());
						estadisticaPorProvinciaYCategoria.setCantidadHechos(max.getValue());
						estadisticaPorProvinciaYCategoria.setFechaGeneracion(LocalDateTime.now());
						estadisticaPorProvinciaYCategoria.setEsUltima(true);
						// crea las hijas de distribucion
						List<EstadisticaProvinciaYCategoriaData> distribucionData = conteoPorProvincia.entrySet().stream()
								.map(e -> {
									EstadisticaProvinciaYCategoriaData data = new EstadisticaProvinciaYCategoriaData();
									data.setProvincia(e.getKey());
									data.setCantidadHechos(e.getValue());
									data.setEstadisticaPorProvinciaYCategoria(estadisticaPorProvinciaYCategoria); // Asignar el padre
									return data;
								}).toList();
						estadisticaPorProvinciaYCategoria.setDistribucionData(distribucionData);
						estadisticaPorProvinciaYCategoriaRepository.save(estadisticaPorProvinciaYCategoria);
					});
		}
	}


	//4) ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
	//@Scheduled(cron = "0 0 * * * *")
	@Scheduled(fixedRate =  120000)
	@Transactional
	public void calcularEstadisticaPorHoraYCategoria() {

		List<HechoDTO> hechos = agregadorClient.getHechos();

		this.marcarHoraYCategoriaComoAntiguas();

		List<String> categoriasUnicas = hechos.stream()
				.map(h -> h.getCategoria())
				.distinct()
				.toList();


		for (String categoriaDeInteres : categoriasUnicas) {

			// de esta categoria, agrupa <Hora, cant de hechos>
			Map<Integer, Long> conteoPorHora = hechos.stream()
					.filter(h -> h.getCategoria().equals(categoriaDeInteres))
					.filter(h -> h.getFechaAcontecimiento() != null)
					.collect(Collectors.groupingBy(h -> h.getFechaAcontecimiento().getHour(), Collectors.counting()));

			// encuentra la hora con la mayor cantidad de hechos para esta categoría
			conteoPorHora.entrySet().stream()
					.max(Map.Entry.comparingByValue())
					.ifPresent(max -> {
						//Crea y carga padre
						EstadisticaPorHoraYCategoria estadisticaPorHoraYCategoria = new EstadisticaPorHoraYCategoria();
						estadisticaPorHoraYCategoria.setCategoria(categoriaDeInteres);
						estadisticaPorHoraYCategoria.setHoraDelDia(max.getKey());
						estadisticaPorHoraYCategoria.setCantidadHechos(max.getValue());
						estadisticaPorHoraYCategoria.setFechaGeneracion(LocalDateTime.now());
						estadisticaPorHoraYCategoria.setEsUltima(true);
						//crea hijas
						List<EstadisticaHoraData> distribucionData = conteoPorHora.entrySet().stream()
								.map(e -> {
									EstadisticaHoraData data = new EstadisticaHoraData();
									data.setHoraDelDia(e.getKey());
									data.setCantidadHechos(e.getValue());
									data.setEstadisticaPorHoraYCategoria(estadisticaPorHoraYCategoria); // Asignar el padre
									return data;
								}).toList();
						estadisticaPorHoraYCategoria.setDistribucionData(distribucionData);
						estadisticaPorHoraYCategoriaRepository.save(estadisticaPorHoraYCategoria);
					});
		}
	}

	// 5) ¿Cuántas solicitudes de eliminación son spam?
	//@Scheduled(cron = "0 0 * * * *")
	@Scheduled(fixedRate =  120000)
	@Transactional
	public void calcularEstadisticaDeSolicitudesSpam() {

		List<RegistroCambioEstadoDTO> cambios = agregadorClient.getRegistrosDeSolicitudes();

		this.marcarSpamComoAntiguas();

		long cantidadSpam = cambios.stream()
				.filter(r -> r.getMotivoRechazo() != null) // es para q no rompa si no hay motivo de rechazo
				.filter(r -> r.getMotivoRechazo().toLowerCase().contains("spam"))
				.map(RegistroCambioEstadoDTO::getNroSolicitudEliminacion) // de todos los q son spam, los transforma a Solicitudes de eliminacion
				.distinct() // busco q las solicitudes no se repitan, sino marcaria la solicitud como spam 2 evces xq pueden haber 2 reg a la misma soli
				.count(); // cuenta la cant d esolicitudes q fueron spam

		long totalSolicitudes = agregadorClient.getSolicitudes().size();

		EstadisticaDeSpam estadisticaDeSpam = new EstadisticaDeSpam();
		estadisticaDeSpam.setCantidadSpam(cantidadSpam);
		estadisticaDeSpam.setTotalSolicitudes(totalSolicitudes);
		estadisticaDeSpam.setFechaGeneracion(LocalDateTime.now());
		estadisticaDeSpam.setEsUltima(true);

		estadisticaDeSpamRepository.save(estadisticaDeSpam);
	}

	private void marcarProvinciaComoAntiguas() {
		List<EstadisticaPorProvincia> estadisticasAntiguas = estadisticaPorProvinciaRepository.findAllByEsUltimaTrue();
		estadisticasAntiguas.forEach(e -> e.setEsUltima(false));
		estadisticaPorProvinciaRepository.saveAll(estadisticasAntiguas);
	}

	private void marcarCategoriaComoAntiguas() {
		List<EstadisticaPorCategoria> estadisticasAntiguas = estadisticaPorCategoriaRepository.findAllByEsUltimaTrue();
		estadisticasAntiguas.forEach(e -> e.setEsUltima(false));
		estadisticaPorCategoriaRepository.saveAll(estadisticasAntiguas);
	}

	private void marcarProvinciaYCategoriaComoAntiguas() {
		List<EstadisticaPorProvinciaYCategoria> estadisticasAntiguas = estadisticaPorProvinciaYCategoriaRepository.findAllByEsUltimaTrue();
		estadisticasAntiguas.forEach(e -> e.setEsUltima(false));
		estadisticaPorProvinciaYCategoriaRepository.saveAll(estadisticasAntiguas);
	}

	private void marcarHoraYCategoriaComoAntiguas() {
		List<EstadisticaPorHoraYCategoria> estadisticasAntiguas = estadisticaPorHoraYCategoriaRepository.findAllByEsUltimaTrue();
		estadisticasAntiguas.forEach(e -> e.setEsUltima(false));
		estadisticaPorHoraYCategoriaRepository.saveAll(estadisticasAntiguas);
	}

	private void marcarSpamComoAntiguas() {
		List<EstadisticaDeSpam> estadisticasAntiguas = estadisticaDeSpamRepository.findAllByEsUltimaTrue();
		estadisticasAntiguas.forEach(e -> e.setEsUltima(false));
		estadisticaDeSpamRepository.saveAll(estadisticasAntiguas);
	}

}
