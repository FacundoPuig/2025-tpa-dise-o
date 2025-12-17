package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
public class AlgoritmosFactory {

  public Algoritmos crearInstancia(String nombreAlgoritmo) {
    if (nombreAlgoritmo == null || nombreAlgoritmo.isBlank()) {
      throw new IllegalArgumentException("El nombre del algoritmo no puede ser nulo o vacío.");
    }

    // Usamos un switch para decidir qué objeto crear
    switch (nombreAlgoritmo.toUpperCase()) {
      case "MAYORIA_ABSOLUTA":
        return new Absoluta();
      case "MAYORIA_SIMPLE":
        return new MayoriaSimple();
      case "MULTIPLES_MENCIONES":
        return new MultiplesMenciones();

      default:
        throw new IllegalArgumentException("Algoritmo desconocido: " + nombreAlgoritmo);
    }
  }
}
