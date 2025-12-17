package ar.utn.ba.dsi.fuenteDinamica.models.repositories;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoRevision;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IHechoDinamicaRepository extends JpaRepository<Hecho, Long> {
  Hecho findByTitulo(String titulo);
  List<Hecho> findByEstadoRevisionAndVisibleTrue(EstadoRevision estadoRevision);
  List<Hecho> findByVisualizadorCreadorId(String visualizadorCreadorId);
  List<Hecho> findByVisibleTrueAndEnviadoFalseAndEstadoRevisionIn(List<EstadoRevision> estadosAceptados);
}