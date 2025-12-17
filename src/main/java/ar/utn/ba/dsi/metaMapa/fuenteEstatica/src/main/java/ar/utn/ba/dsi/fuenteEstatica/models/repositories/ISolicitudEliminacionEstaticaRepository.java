package ar.utn.ba.dsi.fuenteEstatica.models.repositories;

import ar.utn.ba.dsi.fuenteEstatica.models.entities.solicitudes.SolicitudEliminacion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ISolicitudEliminacionEstaticaRepository extends JpaRepository <SolicitudEliminacion, Long> {

}
