/*
package ar.utn.ba.dsi.fuenteDinamica.services.impl;

import ar.utn.ba.dsi.fuenteDinamica.models.dtos.input.HechoDinamicaInputDTO;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Categoria;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoRevision;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IHechoDinamicaRepository;
import ar.utn.ba.dsi.fuenteDinamica.services.IFileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DinamicaServiceTest {

	@Mock private IHechoDinamicaRepository hechoRepository;
	@Mock private IVisualizadorRepository visualizadorRepository;
	@Mock private IFileStorageService fileStorageService;

	@InjectMocks private DinamicaService dinamicaService;

	@Test
	void crear_conDatosCompletos_deberiaGuardarHechoCorrectamente() {
		// Arrange
		HechoDinamicaInputDTO input = new HechoDinamicaInputDTO();
		input.setTitulo("Prueba de Título");
		input.setLatitud(-34.5);
		input.setLongitud(-58.4);
		input.setCategoria(new Categoria(1L, "Incendio"));
		input.setIdVisualizador("user-1");

		MockMultipartFile archivo = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

		when(fileStorageService.guardar(archivo)).thenReturn("unique-file-name.jpg");

		// Act
		dinamicaService.crear(input, archivo);

		// Assert
		// Capturamos el objeto 'Hecho' que se pasó al método save()
		ArgumentCaptor<Hecho> hechoCaptor = ArgumentCaptor.forClass(Hecho.class);
		verify(hechoRepository, times(1)).save(hechoCaptor.capture());

		Hecho hechoGuardado = hechoCaptor.getValue();
		assertEquals("Prueba de Título", hechoGuardado.getTitulo());
		assertEquals(EstadoRevision.PENDIENTE, hechoGuardado.getEstadoRevision());
		assertEquals("unique-file-name.jpg", hechoGuardado.getContenidoMultimedia());
		assertEquals("user-1", hechoGuardado.getOrigen().getNombre());
	}

	@Test
	void crear_sinUbicacion_deberiaLanzarExcepcion() {
		// Arrange
		HechoDinamicaInputDTO input = new HechoDinamicaInputDTO();
		input.setTitulo("Hecho sin ubicación");
		input.setLatitud(null); // Sin latitud

		// Act & Assert
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			dinamicaService.crear(input, null);
		});

		assertEquals("La ubicación debe tener latitud y longitud.", exception.getMessage());
	}
}*/
