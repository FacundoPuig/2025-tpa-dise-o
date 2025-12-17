package ar.utn.ba.dsi.fuenteEstatica.models.repositories.impl;

import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.fuenteEstatica.models.repositories.IHechoEstaticaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class HechoAgregacionRepositoryTest {

	private IHechoEstaticaRepository repository;

	@BeforeEach
	void setUp() {
		// Creamos una nueva instancia del repositorio antes de cada test
		//repository = new IHechoEstaticaRepository();
	}

	@Test
	void save_deberiaAgregarHechoALaLista() {
		Hecho hecho = new Hecho();
		hecho.setTitulo("Nuevo Hecho");

		repository.save(hecho);

		assertFalse(repository.findAll().isEmpty(), "El repositorio no debería estar vacío después de guardar.");
		assertEquals(1, repository.findAll().size(), "El tamaño de la lista de hechos no es el esperado.");
		assertEquals("Nuevo Hecho", repository.findAll().get(0).getTitulo());
	}

	@Test
	void findByTitulo_cuandoHechoExiste_deberiaDevolverlo() {
		Hecho hecho = new Hecho();
		hecho.setTitulo("Hecho Buscado");
		repository.save(hecho);

		Hecho encontrado = repository.findByTitulo("Hecho Buscado");

		assertNotNull(encontrado, "No se encontró el hecho que debería existir.");
		assertEquals("Hecho Buscado", encontrado.getTitulo());
	}

	@Test
	void findByTitulo_cuandoHechoNoExiste_deberiaDevolverNull() {
		Hecho encontrado = repository.findByTitulo("Hecho Inexistente");

		assertNull(encontrado, "Se encontró un hecho que no debería existir.");
	}

	@Test
	void deleteAll_deberiaLimpiarLaListaDeHechos() {
		repository.save(new Hecho());
		repository.save(new Hecho());

		repository.deleteAll();

		assertTrue(repository.findAll().isEmpty(), "El repositorio debería estar vacío después de llamar a deleteAll.");
	}

	@Test
	void findByTitulo_deberiaSerInsensibleAMayusculas() {
		// Arrange
		Hecho hecho = new Hecho();
		hecho.setTitulo("Incendio en Córdoba");
		repository.save(hecho);

		// Act
		Hecho encontradoEnMinusculas = repository.findByTitulo("incendio en córdoba");
		Hecho encontradoEnMayusculas = repository.findByTitulo("INCENDIO EN CÓRDOBA");

		// Assert
		assertNotNull(encontradoEnMinusculas);
		assertNotNull(encontradoEnMayusculas);
		assertEquals("Incendio en Córdoba", encontradoEnMinusculas.getTitulo());
	}

	@Test
	void delete_deberiaQuitarElHechoDeLaLista() {
		// Arrange
		Hecho hechoAEliminar = new Hecho();
		hechoAEliminar.setId(1L);
		hechoAEliminar.setTitulo("Hecho a eliminar");

		Hecho hechoAConservar = new Hecho();
		hechoAConservar.setId(2L);
		hechoAConservar.setTitulo("Hecho a conservar");

		repository.save(hechoAEliminar);
		repository.save(hechoAConservar);

		assertEquals(2, repository.findAll().size()); // Verificación inicial

		// Act
		repository.delete(hechoAEliminar);

		// Assert
		assertEquals(1, repository.findAll().size(), "La lista debería tener solo 1 hecho después de eliminar.");
		assertNull(repository.findByTitulo("Hecho a eliminar"), "El hecho eliminado no debería encontrarse.");
		assertNotNull(repository.findByTitulo("Hecho a conservar"), "El hecho a conservar debería seguir existiendo.");
	}
}