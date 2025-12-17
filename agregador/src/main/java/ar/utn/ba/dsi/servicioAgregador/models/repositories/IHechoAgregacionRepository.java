package ar.utn.ba.dsi.servicioAgregador.models.repositories;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Origen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IHechoAgregacionRepository extends JpaRepository<Hecho, Long> {

  Hecho findByTitulo(String titulo);
  Hecho findByTituloAndOrigen_Nombre(String titulo, String nombreOrigen);
  List<Hecho> findAllByOrigen_ProvieneDeInAndVisibleTrue(Collection<Origen> origenes);


  //Busca hechos uniendo con la colección y soporta paginacion
  @Query("SELECT ch.hecho FROM Coleccion c JOIN c.coleccionHechos ch WHERE c.handleID = :handleId")
  Page<Hecho> findAllByColeccionId(@Param("handleId") String handleId, Pageable pageable);
}