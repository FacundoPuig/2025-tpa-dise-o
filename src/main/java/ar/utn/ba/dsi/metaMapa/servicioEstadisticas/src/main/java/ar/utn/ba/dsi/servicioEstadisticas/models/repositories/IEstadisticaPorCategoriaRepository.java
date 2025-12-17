package ar.utn.ba.dsi.servicioEstadisticas.models.repositories;

import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEstadisticaPorCategoriaRepository extends JpaRepository<EstadisticaPorCategoria, Long> {
  List<EstadisticaPorCategoria> findAllByEsUltimaTrue();
}