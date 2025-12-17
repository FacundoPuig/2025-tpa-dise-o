/*
package ar.utn.ba.dsi.fuenteDinamica.services.impl;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Edicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoEdicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IEdicionRepository;
import ar.utn.ba.dsi.fuenteDinamica.models.repositories.IHechoDinamicaRepository;
import ar.utn.ba.dsi.fuenteDinamica.services.IFileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EdicionServiceTest {

	@Mock private IEdicionRepository edicionRepository;
	@Mock private IHechoDinamicaRepository hechoRepository;
	@Mock private IVisualizadorRepository visualizadorRepository;
	@Mock private IFileStorageService fileStorageService;

	@InjectMocks
	private EdicionService edicionService;

	private Hecho hechoOriginal;
	private Edicion edicionPendiente;
	private Visualizador revisorDePrueba;
	private final String REVISOR_ID = "admin-test";

	@BeforeEach
	void setUp() {
		hechoOriginal = new Hecho();
		hechoOriginal.setId(1L);
		hechoOriginal.setTitulo("Incendio Original");

		edicionPendiente = new Edicion();
		edicionPendiente.setId(10L);
		edicionPendiente.setIdHechoOriginal(hechoOriginal);
		edicionPendiente.setTituloPropuesto("Incendio GRANDE");
		edicionPendiente.setEstado(EstadoEdicion.PENDIENTE);

		revisorDePrueba = new Visualizador();
		revisorDePrueba.setUserId(REVISOR_ID);
		RolUsuario rolAdmin = new RolUsuario("Admin", List.of(Permiso.REVISAR_HECHO));
		revisorDePrueba.setRolUsuario(rolAdmin);
	}

	@Test
	void aceptarEdicion_deberiaActualizarHechoYCambiarEstado() {
		// Arrange: Le damos el guion a los actores
		when(visualizadorRepository.findByUserId(REVISOR_ID)).thenReturn(revisorDePrueba);
		// CORRECCIÓN: Devolvemos el objeto directamente, sin Optional
		when(edicionRepository.findById(10L)).thenReturn(edicionPendiente);
		when(hechoRepository.findById(1L)).thenReturn(hechoOriginal);

		// Act
		edicionService.aceptarEdicion(10L, REVISOR_ID);

		// Assert
		assertEquals(EstadoEdicion.APROBADA, edicionPendiente.getEstado());
		assertEquals("Incendio GRANDE", hechoOriginal.getTitulo());
		// CORRECCIÓN: Usamos .update() porque tus repos en memoria tienen ese método
		verify(edicionRepository, times(1)).update(edicionPendiente);
		verify(hechoRepository, times(1)).update(hechoOriginal);
	}

	@Test
	void rechazarEdicion_deberiaCambiarEstadoSinModificarHecho() {
		// Arrange: Le damos el guion a los actores
		when(visualizadorRepository.findByUserId(REVISOR_ID)).thenReturn(revisorDePrueba);
		// CORRECCIÓN: Devolvemos el objeto directamente, sin Optional
		when(edicionRepository.findById(10L)).thenReturn(edicionPendiente);

		// Act
		edicionService.rechazarEdicion(10L, REVISOR_ID);

		// Assert
		assertEquals(EstadoEdicion.RECHAZADA, edicionPendiente.getEstado());
		assertEquals("Incendio Original", hechoOriginal.getTitulo());
		// CORRECCIÓN: Usamos .update()
		verify(edicionRepository, times(1)).update(edicionPendiente);
		verify(hechoRepository, never()).update(any(Hecho.class));
	}
}*/
