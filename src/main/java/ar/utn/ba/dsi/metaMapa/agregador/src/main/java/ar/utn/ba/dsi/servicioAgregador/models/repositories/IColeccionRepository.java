package ar.utn.ba.dsi.servicioAgregador.models.repositories;

import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IColeccionRepository extends JpaRepository<Coleccion, String> {
  /*public void save(Coleccion coleccion);
  public List<Coleccion> findAll();
  public Coleccion findById(String id);
  public void delete(Coleccion coleccion);
  */

  /*
  * nuestro save()
  *
    public void save(Coleccion coleccion) {
    if (coleccion.getHandleID() == null || coleccion.getHandleID().isEmpty()) {
      coleccion.setHandleID(generarHandleUnico(coleccion.getTitulo()));
    }

    // Eliminar cualquier existente con el mismo ID
    this.colecciones.removeIf(c -> c.getHandleID().equals(coleccion.getHandleID()));

    // Agregar la nueva o actualizada
    this.colecciones.add(coleccion);
  }

    private String generarHandleUnico(String titulo) {
    String base = titulo.toLowerCase()
        .replaceAll("[^a-z0-9\\s]", "")  // elimina lo que no sea letras, números o espacios.
        .replaceAll("\\s+", "-");        // reemplaza espacios por guiones

    String nuevoHandle = base;
    int contador = 1;

    while (existeHandle(nuevoHandle)) {
      nuevoHandle = base + "-" + contador++;
    }

    return nuevoHandle;
  }

  private boolean existeHandle(String handle) {
    return this.colecciones.stream()
        .anyMatch(c -> c.getHandleID().equals(handle));
  }
  *
  * Ese chequeo deberia ir en algun service, dejarle solo al repo la accion de guardar
  * */

}
