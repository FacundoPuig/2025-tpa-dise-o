package ar.utn.ba.dsi.servicioAgregador.models.repositories;

import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.fuentes.Fuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFuenteRepository extends JpaRepository<Fuente, Long> {
  Optional<Fuente> findByNombreFuenteIgnoreCase(String nombreFuente);
}
