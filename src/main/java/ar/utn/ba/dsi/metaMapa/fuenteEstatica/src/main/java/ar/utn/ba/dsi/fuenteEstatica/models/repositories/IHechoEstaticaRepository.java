package ar.utn.ba.dsi.fuenteEstatica.models.repositories;

import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.EstadoRevision;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos.Hecho;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface IHechoEstaticaRepository extends JpaRepository<Hecho, Long> {
  public Hecho findByTitulo(String titulo);
  List<Hecho> findByVisibleTrueAndEnviadoFalse();

}
