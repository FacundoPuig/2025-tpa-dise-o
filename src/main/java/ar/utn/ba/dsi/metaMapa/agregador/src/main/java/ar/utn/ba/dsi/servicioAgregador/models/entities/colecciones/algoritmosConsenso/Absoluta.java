package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso;

import ar.utn.ba.dsi.servicioAgregador.models.entities.intermedia.ColeccionHecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Origen;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Si todas las fuentes contienen el mismo hecho, se lo considera consensuado.
public class Absoluta implements Algoritmos {

  @Override
  public void cumpleConConcenso(List<ColeccionHecho> itemsColeccion) {

    if (itemsColeccion == null || itemsColeccion.isEmpty())
      return;

    // 1. Identificar cuántas fuentes distintas hay en TOTAL en esta colección
    // (Esto asume que queremos consenso sobre las fuentes que 'trajeron algo',
    // si quisiéramos sobre las configuradas, deberíamos recibir el objeto Coleccion).
    long cantidadFuentesTotales = itemsColeccion.stream()
        .map(item -> item.getHecho().getOrigen().getNombre())
        .distinct()
        .count();

    // 2. Agrupar las relaciones por Título del Hecho
    Map<String, List<ColeccionHecho>> agrupadosPorTitulo = itemsColeccion.stream()
        .collect(Collectors.groupingBy(item -> item.getHecho().getTitulo()));

    // 3. Evaluar cada grupo
    agrupadosPorTitulo.values().forEach(grupo -> {

      // Contar cuántas fuentes distintas aportaron este título
      long fuentesDelHecho = grupo.stream()
          .map(item -> item.getHecho().getOrigen().getNombre())
          .distinct()
          .count();

      // Si las fuentes del hecho son iguales a las fuentes totales de la colección -> Consenso
      boolean esConsensuado = fuentesDelHecho == cantidadFuentesTotales;

      // Guardar resultado en la tabla intermedia
      grupo.forEach(item -> item.setEsConsensuado(esConsensuado));
    });
  }
}