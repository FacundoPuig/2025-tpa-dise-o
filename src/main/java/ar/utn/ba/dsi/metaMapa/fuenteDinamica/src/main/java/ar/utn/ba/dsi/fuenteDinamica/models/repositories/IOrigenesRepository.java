package ar.utn.ba.dsi.fuenteDinamica.models.repositories;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Origenes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOrigenesRepository extends JpaRepository<Origenes, Integer> {
	Optional<Origenes> findByNombre(String nombre);
}