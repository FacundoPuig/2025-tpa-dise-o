package ar.utn.ba.dsi.fuenteEstatica.models.entities.fileReader;

import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Hecho;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class FileReaderTest {

	private FileReader fileReader;

	// JUnit creará una carpeta temporal para nosotros antes de cada prueba y la limpiará después
	@TempDir
	Path tempDir;

	private Path carpetaEntrada;
	private Path carpetaSalida;

	@BeforeEach
	void setUp() {
		// Configuramos el FileReader para que use nuestras carpetas temporales
		carpetaEntrada = tempDir.resolve("forProcessing");
		carpetaSalida = tempDir.resolve("Processed");

		// Pasamos las rutas como String al constructor, como en tu clase original
		fileReader = new FileReader(carpetaEntrada.toString(), carpetaSalida.toString());
	}

	@Test
	void cargarCSV_deberiaGuardarArchivoEnCarpetaDeEntrada() throws IOException {
		// Arrange: Preparamos un archivo simulado (mock)
		MockMultipartFile mockFile = new MockMultipartFile(
				"file",
				"hechos.csv",
				"text/csv",
				"test data".getBytes()
		);

		// Act: Ejecutamos el método que queremos probar
		fileReader.cargarCSV(mockFile);

		// Assert: Verificamos que el archivo realmente existe en la carpeta de entrada
		Path archivoEsperado = carpetaEntrada.resolve("hechos.csv");
		assertTrue(Files.exists(archivoEsperado), "El archivo CSV no fue guardado en la carpeta de entrada.");
	}

	@Test
	void leerHechosDesdeCSV_deberiaLeerParsearYMoverElArchivo() throws IOException {
		// Arrange: Creamos un archivo CSV de prueba
		String contenidoCSV = "Título,Descripción,Categoría,Latitud,Longitud,Fecha del hecho\n"
				+ "\"Incendio en el Amazonas, Fase 2\",Fue un gran incendio,Incendio,-10.333,-55.5,15/08/2025\n"
				+ "Sequía en el Paraná,El río está bajo,Sequía,-27.46,-58.8,01/01/2025";

		Path archivoCSV = carpetaEntrada.resolve("datos.csv");
		Files.write(archivoCSV, contenidoCSV.getBytes());

		// Act: Ejecutamos la lectura
		List<Hecho> hechos = fileReader.leerHechosDesdeCSV();

		// Assert: Verificamos los resultados
		// 1. Que la lista de hechos tenga el tamaño correcto
		assertEquals(2, hechos.size(), "La cantidad de hechos leídos no es la correcta.");

		// 2. Que los datos del primer hecho sean correctos (incluyendo el título con comas)
		Hecho primerHecho = hechos.get(0);
		assertEquals("Incendio en el Amazonas, Fase 2", primerHecho.getTitulo());
		assertEquals("Incendio", primerHecho.getCategoria().getNombre());
		assertEquals(-10.333, primerHecho.getUbicacion().getLatitud());

		// 3. Que el archivo original haya sido movido a la carpeta de procesados
		assertTrue(Files.exists(carpetaSalida.resolve("datos.csv")), "El archivo no se movió a la carpeta de procesados.");
		assertFalse(Files.exists(archivoCSV), "El archivo original no se eliminó de la carpeta de entrada.");
	}

	@Test
	void leerHechosDesdeCSV_cuandoNoHayArchivos_deberiaDevolverNull() {
		// Act: Llamamos al método cuando la carpeta de entrada está vacía
		List<Hecho> hechos = fileReader.leerHechosDesdeCSV();

		// Assert: Verificamos que devuelva null, como en tu implementación
		assertNull(hechos, "Debería devolver null si no hay archivos CSV para procesar.");
	}

	@Test
	void leerHechosDesdeCSV_conArchivoRealDe15000Hechos_deberiaProcesarlosTodos() throws Exception { // Puede lanzar Exception
		// 1. Ubicamos el archivo real que está en 'src/test/resources'.
		// Esto es mucho más robusto que usar rutas absolutas.
		URI uriArchivoReal = getClass().getClassLoader().getResource("desastres_naturales_argentina.csv").toURI();
		Path archivoReal = Paths.get(uriArchivoReal);

		// 2. Copiamos el archivo real a la carpeta de entrada temporal de nuestra prueba.
		// Así, el archivo original no se modifica ni se mueve.
		Path archivoDePrueba = carpetaEntrada.resolve("desastres_naturales_argentina.csv");
		Files.copy(archivoReal, archivoDePrueba, StandardCopyOption.REPLACE_EXISTING);

		// Act: Ejecutamos el método a probar
		List<Hecho> hechos = fileReader.leerHechosDesdeCSV();

		// Assert: Verificamos los resultados
		// 1. Verificamos que la lista no sea nula
		assertNotNull(hechos, "La lista de hechos no debería ser nula.");

		// 2. Comprobamos que se hayan leído los 15,000 hechos
		assertEquals(15000, hechos.size(), "La cantidad de hechos procesados no coincide con las 15,000 esperadas.");

		// 3. Verificamos un dato del primer hecho para asegurar que el parseo fue correcto
		Hecho primerHecho = hechos.get(0);
		assertNotNull(primerHecho.getTitulo(), "El título del primer hecho no debería ser nulo.");

		// 4. Verificamos que el título del primer hecho sea el esperado
		assertEquals("Ráfagas de más de 100 km/h causa estragos en San Vicente, Misiones", primerHecho.getTitulo());

		// 4. Verificamos que el archivo fue movido correctamente después de ser procesado.
		assertTrue(Files.exists(carpetaSalida.resolve("desastres_naturales_argentina.csv")), "El archivo no se movió a la carpeta de procesados.");
		assertFalse(Files.exists(archivoDePrueba), "El archivo de prueba no fue eliminado de la carpeta de entrada.");
	}

	@Test
	void leerHechosDesdeCSV_conLineasCorruptas_deberiaOmitirlasYProcesarLasValidas() throws IOException {
		// Arrange: Creamos un CSV con una mezcla de líneas buenas y malas.
		String contenidoCSV = "Título,Descripción,Categoría,Latitud,Longitud,Fecha del hecho\n"
				+ "Hecho Válido 1,Datos correctos,Categoría A,-34.5,-58.4,01/01/2025\n"
				+ "Hecho Malo 1,Latitud inválida,Categoría B,esto-no-es-numero,-58.5,02/01/2025\n"
				+ "Hecho Válido 2,Datos correctos de nuevo,Categoría C,-34.6,-58.6,03/01/2025\n"
				+ "Hecho Malo 2,Faltan columnas,Categoría D,04/01/2025\n";

		Path archivoDePrueba = carpetaEntrada.resolve("datos_mixtos.csv");
		Files.write(archivoDePrueba, contenidoCSV.getBytes());

		// Act: Ejecutamos el método a probar.
		List<Hecho> hechos = fileReader.leerHechosDesdeCSV();

		// Assert: Verificamos que solo se procesaron las líneas válidas.
		assertNotNull(hechos);
		assertEquals(2, hechos.size(), "Solo se deberían haber procesado las 2 líneas válidas.");
		assertEquals("Hecho Válido 1", hechos.get(0).getTitulo());
		assertEquals("Hecho Válido 2", hechos.get(1).getTitulo());

		// Y que el archivo, a pesar de los errores, fue movido.
		assertTrue(Files.exists(carpetaSalida.resolve("datos_mixtos.csv")));
	}


}