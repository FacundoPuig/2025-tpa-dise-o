package ar.utn.ba.dsi.servicioEstadisticas.models.repositories;

import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorProvinciaYCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEstadisticaPorProvinciaYCategoriaRepository extends JpaRepository<EstadisticaPorProvinciaYCategoria, Long> {
  List<EstadisticaPorProvinciaYCategoria> findAllByEsUltimaTrue();
  Optional<EstadisticaPorProvinciaYCategoria> findByCategoriaAndEsUltimaTrue(String categoria);
}