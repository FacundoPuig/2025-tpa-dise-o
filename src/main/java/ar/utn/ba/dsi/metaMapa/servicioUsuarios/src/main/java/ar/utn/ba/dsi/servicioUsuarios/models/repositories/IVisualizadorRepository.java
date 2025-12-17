package ar.utn.ba.dsi.servicioUsuarios.models.repositories;

import ar.utn.ba.dsi.servicioUsuarios.models.entities.Visualizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IVisualizadorRepository extends JpaRepository<Visualizador, Long> {

	/**
	 * Busca un Visualizador por el email de su UsuarioFisico asociado.
	 */
	Visualizador findByUsuario_Email(String email);
}