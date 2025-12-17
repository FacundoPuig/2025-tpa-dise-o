package ar.utn.ba.dsi.fuenteDinamica.models.repositories;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {
	Optional<Categoria> findByNombre(String nombre);
}