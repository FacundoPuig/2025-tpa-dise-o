/*
package ar.utn.ba.dsi.fuenteDinamica.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {

	@TempDir
	Path tempDir; // JUnit crea y limpia esta carpeta por nosotros

	private FileStorageService fileStorageService;

	@BeforeEach
	void setUp() {
		fileStorageService = new FileStorageService(tempDir.toString());
	}

	@Test
	void guardar_archivoValido_deberiaCrearArchivoEnDirectorio() throws IOException {
		// Arrange
		MockMultipartFile archivo = new MockMultipartFile("file", "test.txt", "text/plain", "Hola Mundo".getBytes());

		// Act
		String nombreGuardado = fileStorageService.guardar(archivo);

		// Assert
		assertNotNull(nombreGuardado);
		assertTrue(nombreGuardado.endsWith(".txt"));
		assertTrue(Files.exists(tempDir.resolve(nombreGuardado)));
		assertEquals("Hola Mundo", Files.readString(tempDir.resolve(nombreGuardado)));
	}

	@Test
	void eliminar_archivoExistente_deberiaBorrarlo() throws IOException {
		// Arrange: creamos un archivo primero
		Path archivoExistente = tempDir.resolve("archivo_a_borrar.txt");
		Files.write(archivoExistente, "datos".getBytes());
		assertTrue(Files.exists(archivoExistente));

		// Act
		fileStorageService.eliminar("archivo_a_borrar.txt");

		// Assert
		assertFalse(Files.exists(archivoExistente));
	}
}*/
