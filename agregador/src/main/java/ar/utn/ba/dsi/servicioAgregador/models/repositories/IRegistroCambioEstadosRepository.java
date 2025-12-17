package ar.utn.ba.dsi.servicioAgregador.models.repositories;

import ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes.RegistroCambioEstado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRegistroCambioEstadosRepository extends JpaRepository<RegistroCambioEstado, Long> {
}
