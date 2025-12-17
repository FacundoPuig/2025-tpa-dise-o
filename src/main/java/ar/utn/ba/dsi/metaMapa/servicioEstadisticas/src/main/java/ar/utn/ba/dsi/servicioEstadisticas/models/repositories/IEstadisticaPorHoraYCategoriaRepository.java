package ar.utn.ba.dsi.servicioEstadisticas.models.repositories;

import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaPorHoraYCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEstadisticaPorHoraYCategoriaRepository extends JpaRepository<EstadisticaPorHoraYCategoria, Long> {
  Optional<EstadisticaPorHoraYCategoria> findByCategoriaAndEsUltimaTrue(String categoria);
  List<EstadisticaPorHoraYCategoria> findAllByEsUltimaTrue();
}