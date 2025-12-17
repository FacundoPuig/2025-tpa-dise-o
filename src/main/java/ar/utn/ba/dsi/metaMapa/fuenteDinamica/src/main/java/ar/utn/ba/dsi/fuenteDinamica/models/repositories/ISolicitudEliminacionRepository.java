package ar.utn.ba.dsi.fuenteDinamica.models.repositories;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoRevision;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.SolicitudEliminacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISolicitudEliminacionRepository extends JpaRepository<SolicitudEliminacion, Long> {
	List<SolicitudEliminacion> findByEstado(EstadoRevision estado);
}