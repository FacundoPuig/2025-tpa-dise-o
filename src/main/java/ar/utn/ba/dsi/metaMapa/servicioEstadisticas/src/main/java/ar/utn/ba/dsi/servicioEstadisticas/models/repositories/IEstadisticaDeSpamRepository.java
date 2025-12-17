package ar.utn.ba.dsi.servicioEstadisticas.models.repositories;

import ar.utn.ba.dsi.servicioEstadisticas.models.entities.EstadisticaDeSpam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEstadisticaDeSpamRepository extends JpaRepository<EstadisticaDeSpam, Long> {
  Optional<EstadisticaDeSpam> findFirstByOrderByFechaGeneracionDesc();
  List<EstadisticaDeSpam> findAllByEsUltimaTrue();
}