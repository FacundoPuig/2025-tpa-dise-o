package ar.utn.ba.dsi.servicioEstadisticas.services;

import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaCategoriaData;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaDeSpam;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaHoraData;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorHoraYCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorProvincia;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorProvinciaYCategoria;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaProvinciaData;
import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaProvinciaYCategoriaData;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaDeSpamRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorCategoriaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorHoraYCategoriaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorProvinciaRepository;
import ar.utn.ba.dsi.servicioEstadisticas.models.repositories.IEstadisticaPorProvinciaYCategoriaRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class ExportadorCSV {
	@Autowired
	private IEstadisticaPorProvinciaRepository EstadisticaPorProvinciaRepository;
	@Autowired
	private IEstadisticaPorCategoriaRepository EstadisticaPorCategoriaRepository;
	@Autowired
	private IEstadisticaPorProvinciaYCategoriaRepository EstadisticaPorProvinciaYCategoriaRepository;
	@Autowired
	private IEstadisticaPorHoraYCategoriaRepository EstadisticaPorHoraYCategoriaRepository;
	@Autowired
	private IEstadisticaDeSpamRepository EstadisticaDeSpamRepository;

	private static final char SEPARATOR = ',';

	// Exportar Estadística 1: Provincia por Colección (con historial)
	public String exportarEstadisticaPorProvinciaACsv() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8), SEPARATOR,
						 CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] header = {"Fecha Generacion", "Es Ultima", "Handle ID Coleccion", "Coleccion", "Provincia (Dato)", "Cantidad de Hechos (Dato)"};
			csvWriter.writeNext(header);

			List<EstadisticaPorProvincia> padres = EstadisticaPorProvinciaRepository.findAll();

			for (EstadisticaPorProvincia padre : padres) {
				// Itera sobre la distribución completa (Entidades Hijas)
				for (EstadisticaProvinciaData data : padre.getDistribucionData()) {
					String[] row = {
							padre.getFechaGeneracion().toString(),
							String.valueOf(padre.isEsUltima()),
							padre.getColeccionHandle(),
							padre.getColeccionTitulo(),
							data.getProvincia(),
							String.valueOf(data.getCantidadHechos())
					};
					csvWriter.writeNext(row);
				}
			}
			csvWriter.flush();
			return baos.toString(StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new RuntimeException("Error al exportar la estadística de provincia por colección a CSV", e);
		}
	}

	// Exportar Estadística 2: Categoría con más Hechos
	public String exportarEstadisticaPorCategoriaACsv() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8), SEPARATOR,
						 CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] header = {"Fecha Generacion", "Es Ultima", "Categoria (Dato)", "Cantidad de Hechos (Dato)"};
			csvWriter.writeNext(header);

			List<EstadisticaPorCategoria> padres = EstadisticaPorCategoriaRepository.findAll();

			for (EstadisticaPorCategoria padre : padres) {
				for (EstadisticaCategoriaData data : padre.getDistribucionData()) {
					String[] row = {
							padre.getFechaGeneracion().toString(),
							String.valueOf(padre.isEsUltima()),
							data.getCategoria(),
							String.valueOf(data.getCantidadHechos())
					};
					csvWriter.writeNext(row);
				}
			}
			csvWriter.flush();
			return baos.toString(StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new RuntimeException("Error al exportar la estadística de categoría a CSV", e);
		}
	}

	// Exportar Estadística 3: Provincia por Categoría
	public String exportarEstadisticaPorProvinciaYCategoriaACsv() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8), SEPARATOR,
						 CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] header = {"Fecha Generacion", "Es Ultima", "Categoria (Filtro)", "Provincia (Dato)", "Cantidad de Hechos (Dato)"};
			csvWriter.writeNext(header);

			List<EstadisticaPorProvinciaYCategoria> padres = EstadisticaPorProvinciaYCategoriaRepository.findAll();

			for (EstadisticaPorProvinciaYCategoria padre : padres) {
				for (EstadisticaProvinciaYCategoriaData data : padre.getDistribucionData()) {
					String[] row = {
							padre.getFechaGeneracion().toString(),
							String.valueOf(padre.isEsUltima()),
							padre.getCategoria(), // La categoría es el filtro del padre
							data.getProvincia(),
							String.valueOf(data.getCantidadHechos())
					};
					csvWriter.writeNext(row);
				}
			}
			csvWriter.flush();
			return baos.toString(StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new RuntimeException("Error al exportar la estadística de provincia por categoría a CSV", e);
		}
	}

	// Exportar Estadística 4: Hora por Categoría
	public String exportarEstadisticaPorHoraYCategoriaACsv() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8), SEPARATOR,
						 CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] header = {"Fecha Generacion", "Es Ultima", "Categoria (Filtro)", "Hora del Día (Dato)", "Cantidad de Hechos (Dato)"};
			csvWriter.writeNext(header);

			List<EstadisticaPorHoraYCategoria> padres = EstadisticaPorHoraYCategoriaRepository.findAll();

			for (EstadisticaPorHoraYCategoria padre : padres) {
				for (EstadisticaHoraData data : padre.getDistribucionData()) {
					String[] row = {
							padre.getFechaGeneracion().toString(),
							String.valueOf(padre.isEsUltima()),
							padre.getCategoria(), // La categoría es el filtro del padre
							String.valueOf(data.getHoraDelDia()),
							String.valueOf(data.getCantidadHechos())
					};
					csvWriter.writeNext(row);
				}
			}
			csvWriter.flush();
			return baos.toString(StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new RuntimeException("Error al exportar la estadística de hora por categoría a CSV", e);
		}
	}

	// Exportar Estadística 5: Spam
	public String exportarEstadisticaDeSpamACsv() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				 CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8), SEPARATOR,
						 CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] header = {"Fecha Generacion", "Es Ultima", "Cantidad de Solicitudes Spam", "Total Solicitudes"};
			csvWriter.writeNext(header);

			List<EstadisticaDeSpam> estadisticas = EstadisticaDeSpamRepository.findAll();

			for (EstadisticaDeSpam s : estadisticas) {
				String[] row = {
						s.getFechaGeneracion().toString(),
						String.valueOf(s.isEsUltima()),
						String.valueOf(s.getCantidadSpam()),
						String.valueOf(s.getTotalSolicitudes())
				};
				csvWriter.writeNext(row);
			}
			csvWriter.flush();
			return baos.toString(StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new RuntimeException("Error al exportar la estadística de spam a CSV", e);
		}
	}

	// Exportar TODAS las Estadísticas en formato ZIP
	public byte[] exportarTodasLasEstadisticasAZip() throws IOException {
		List<String> coleccionDeEstadisticas = List.of(
				exportarEstadisticaPorProvinciaACsv(),
				exportarEstadisticaPorCategoriaACsv(),
				exportarEstadisticaPorProvinciaYCategoriaACsv(),
				exportarEstadisticaPorHoraYCategoriaACsv(),
				exportarEstadisticaDeSpamACsv()
		);

		List<String> nombresArchivos = List.of(
				"Historial_Provincia_Coleccion.csv",
				"Historial_Categoria_Global.csv",
				"Historial_Provincia_Por_Categoria.csv",
				"Historial_Hora_Por_Categoria.csv",
				"Historial_Solicitudes_Spam.csv"
		);

		if (coleccionDeEstadisticas.size() != nombresArchivos.size()) {
			throw new IllegalStateException("Cantidad de estadísticas y nombres de archivo no coincide.");
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			for (int i = 0; i < coleccionDeEstadisticas.size(); i++) {
				ZipEntry entry = new ZipEntry(nombresArchivos.get(i));
				zos.putNextEntry(entry);
				zos.write(coleccionDeEstadisticas.get(i).getBytes(StandardCharsets.UTF_8));
				zos.closeEntry();
			}
		}

		return baos.toByteArray();
	}

}
