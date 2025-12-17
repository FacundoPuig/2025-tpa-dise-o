package ar.utn.ba.dsi.servicioAgregador.models.repositories;

import ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios.Visualizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
// OJO: Cambié <Visualizador, String> a <Visualizador, Long> porque tu ID es long
public interface IVisualizadorRepository extends JpaRepository<Visualizador, Long> {
	Optional<Visualizador> findByUsuario_Email(String email);
}