// java
package ar.utn.ba.dsi.fuenteEstatica.models.entities.fileReader;

import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Categoria;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Origenes;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Ubicacion;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Component
public class FileReader {

	private final Path carpetaEntrada;
	private final Path carpetaSalida;


	public FileReader(@Value("${fuente.carpeta.entrada}") String rutaEntrada,
										@Value("${fuente.carpeta.salida}") String rutaSalida) {
		this.carpetaEntrada = Paths.get(rutaEntrada);
		this.carpetaSalida = Paths.get(rutaSalida);
		try {
			Files.createDirectories(this.carpetaEntrada);
			Files.createDirectories(this.carpetaSalida);
		} catch (IOException e) {
			throw new RuntimeException("No se pudieron crear las carpetas para el FileReader", e);
		}
	}

	// ver de mas adelante intentar que guarde multiples archivos a la vez
	public void cargarCSV(MultipartFile file) {
		try {
			Path destino = this.carpetaEntrada.resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("✅ CSV cargado en: " + destino);
		} catch (IOException e) {
			throw new RuntimeException("Error al cargar el archivo CSV", e);
		}
	}

	public boolean archivoYaFueProcesado(String nombreArchivo) {
		// Busca si el archivo existe en la carpeta de salida
		Path pathArchivo = this.carpetaSalida.resolve(nombreArchivo);
		return Files.exists(pathArchivo);
	}

	private List<String> parsearLineaCSV(String linea) {
		List<String> campos = new ArrayList<>();
		StringBuilder campoActual = new StringBuilder();
		boolean dentroDeComillas = false;
		for (char c : linea.toCharArray()) {
			if (c == '"') {
				dentroDeComillas = !dentroDeComillas;
			} else if (c == ',' && !dentroDeComillas) {
				campos.add(campoActual.toString().trim());
				campoActual.setLength(0);
			} else {
				campoActual.append(c);
			}
		}
		campos.add(campoActual.toString().trim());
		return campos;
	}

	public List<Hecho> leerHechosDesdeCSV() {
		List<Hecho> hechos = new ArrayList<>();
		DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<Path> archivosCSV;
		try (var stream = Files.newDirectoryStream(carpetaEntrada, "*.{csv,CSV}")) {
			archivosCSV = StreamSupport.stream(stream.spliterator(), false).collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException("Error al acceder a la carpeta de entrada", e);
		}

		if (archivosCSV.isEmpty()) {
			System.out.println("No hay archivos CSV en la carpeta de entrada.");
			return null;
		}

		archivosCSV.forEach(archivoPath -> {
			System.out.println("📂 Procesando archivo: " + archivoPath.getFileName());
			try {
				List<String> lineas = Files.readAllLines(archivoPath);
				lineas.stream().skip(1).forEach(linea -> {
					List<String> campos = parsearLineaCSV(linea);
					if (campos.size() >= 6) {
						try {
							String titulo = campos.get(0).replaceAll("^\"|\"$", "");
							String descripcion = campos.get(1);
							String categoria = campos.get(2);
							double latitud = Double.parseDouble(campos.get(3));
							double longitud = Double.parseDouble(campos.get(4));

							LocalDate fechaSimple = LocalDate.parse(campos.get(5), formatoFecha);
							LocalDateTime fechaCompleta = fechaSimple.atStartOfDay();

							String contenido = campos.size() > 6 ? campos.get(6) : "";

							Hecho hecho = new Hecho(titulo, descripcion, new Categoria(categoria), fechaCompleta, contenido, new Origenes(archivoPath.getFileName().toString()), new Ubicacion(latitud, longitud));
							hechos.add(hecho);
						} catch(DateTimeParseException e) {
							System.err.println("Error de formato de fecha en la línea: " + linea + ". Se omitirá.");
						} catch(NumberFormatException e) {
							System.err.println("Error de formato de número (lat/long) en la línea: " + linea + ". Se omitirá.");
						}
					}
				});

				Path destinoProcesado = carpetaSalida.resolve(archivoPath.getFileName());
				Files.move(archivoPath, destinoProcesado, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("✅ Archivo movido a carpeta Processed: " + destinoProcesado);

			} catch (IOException e) {
				throw new RuntimeException("Error al leer o mover el archivo: " + archivoPath.getFileName(), e);
			}
		});

		return hechos;
	}
}