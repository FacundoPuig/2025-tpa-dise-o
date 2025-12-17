package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso;

import ar.utn.ba.dsi.servicioAgregador.models.entities.intermedia.ColeccionHecho;
import java.util.List;
import java.util.stream.Collectors;

public class MayoriaSimple implements Algoritmos {

  @Override
  public void cumpleConConcenso(List<ColeccionHecho> itemsColeccion) {

    if (itemsColeccion == null || itemsColeccion.isEmpty()) return;

    // 1. Contar fuentes únicas iterando sobre la relación
    long cantidadFuentes = itemsColeccion.stream()
        .map(item -> item.getHecho().getOrigen().getNombre()) // Acceder al hecho
        .distinct()
        .count();

    System.out.println("Cantidad de fuentes distintas en la colección: " + cantidadFuentes);

    long minimoParaConsenso = (long) Math.ceil(cantidadFuentes / 2.0);

    System.out.println("Cantidad mínima de fuentes para consenso (mayoría simple): " + minimoParaConsenso);

    // 2. Agrupar las RELACIONES (ColeccionHecho) por el título del hecho
    itemsColeccion.stream()
        .collect(Collectors.groupingBy(item -> item.getHecho().getTitulo()))
        .values()
        .forEach(grupoDeItems -> {
          System.out.println("Evaluando título: " + grupoDeItems.get(0).getHecho().getTitulo() + " con " + grupoDeItems.size() + " registros.");

          // 3. Calcular fuentes distintas dentro de este grupo
          long fuentesDistintas = grupoDeItems.stream()
              .map(item -> item.getHecho().getOrigen().getNombre())
              .distinct()
              .count();

          // 4. Aplicar el consenso SOBRE LA RELACIÓN (tabla intermedia)
          boolean hayConsenso = fuentesDistintas >= minimoParaConsenso;

          // Esto guarda "true" o "false" en la tabla coleccion_hecho,
          // sin afectar al hecho globalmente.
          grupoDeItems.forEach(item -> item.setEsConsensuado(hayConsenso));
        });
  }
}