package ar.utn.ba.dsi.fuenteDinamica.models.repositories;

import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Edicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.EstadoEdicion;
import ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEdicionRepository extends JpaRepository<Edicion, Long> {

	List<Edicion> findByEstado(EstadoEdicion estado);

	// Query custom para buscar si hay edición pendiente para un hecho específico
	@Query("SELECT e FROM Edicion e WHERE e.idHechoOriginal.id = :idHecho AND e.estado = 'PENDIENTE'")
	Optional<Edicion> findPendingByHechoId(long idHecho);

	List<Edicion> findByVisualizadorEditorId(String visualizadorEditorId);
	boolean existsByIdHechoOriginal_IdAndEstado(Long idHecho, EstadoEdicion estado);
	List<Edicion> findByIdHechoOriginal_Id(Long idHechoOriginal);
}