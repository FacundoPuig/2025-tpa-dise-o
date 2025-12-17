package ar.utn.ba.dsi.servicioAgregador.models.repositories;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.Estados;
import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.SolicitudEliminacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ISolicitudEliminacionAgregacionRepository extends JpaRepository<SolicitudEliminacion, Integer> {
	List<SolicitudEliminacion> findBySolicitanteId(String solicitanteId);
	List<SolicitudEliminacion> findByEstado(Estados estado);
}
