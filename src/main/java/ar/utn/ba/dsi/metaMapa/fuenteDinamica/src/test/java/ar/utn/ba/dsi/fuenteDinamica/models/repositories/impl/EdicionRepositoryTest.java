/*
package ar.utn.ba.dsi.fuenteDinamica.models.repositories.impl;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Edicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoEdicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EdicionRepositoryTest {

	private EdicionRepository edicionRepository;
	private Hecho hecho1;
	private Hecho hecho2;

	@BeforeEach
	void setUp() {
		edicionRepository = new EdicionRepository();

		hecho1 = new Hecho();
		hecho1.setId(1L);
		hecho2 = new Hecho();
		hecho2.setId(2L);

		// Creamos ediciones de prueba
		Edicion edicion1 = new Edicion();
		edicion1.setIdHechoOriginal(hecho1);
		edicion1.setEstado(EstadoEdicion.PENDIENTE);

		Edicion edicion2 = new Edicion();
		edicion2.setIdHechoOriginal(hecho1);
		edicion2.setEstado(EstadoEdicion.APROBADA);

		Edicion edicion3 = new Edicion();
		edicion3.setIdHechoOriginal(hecho2);
		edicion3.setEstado(EstadoEdicion.PENDIENTE);

		// Las guardamos
		edicionRepository.save(edicion1);
		edicionRepository.save(edicion2);
		edicionRepository.save(edicion3);
	}

	@Test
	void findPendingByHechoId_cuandoExistePendiente_deberiaDevolverla() {
		// Act
		Edicion encontrada = edicionRepository.findPendingByHechoId(1L);

		// Assert
		assertNotNull(encontrada);
		assertEquals(EstadoEdicion.PENDIENTE, encontrada.getEstado());
		assertEquals(1L, encontrada.getIdHechoOriginal().getId());
	}

	@Test
	void findPendingByHechoId_cuandoNoExistePendiente_deberiaDevolverNull() {
		// Arrange: Creamos un hecho sin ediciones pendientes
		Hecho hechoSinPendientes = new Hecho();
		hechoSinPendientes.setId(3L);

		// Act
		Edicion encontrada = edicionRepository.findPendingByHechoId(3L);

		// Assert
		assertNull(encontrada);
	}
}*/
