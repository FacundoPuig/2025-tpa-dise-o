package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.intermedia.ColeccionHecho;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiplesMenciones implements Algoritmos {

  @Override
  public void cumpleConConcenso(List<ColeccionHecho> itemsColeccion) {

    System.out.println(">>> Ejecutando algoritmo: Múltiples Menciones");

    if (itemsColeccion == null || itemsColeccion.isEmpty()) return;

    // 1. Agrupar por título (Usando LOWERCASE para evitar errores por mayúsculas/minúsculas)
    // Ejemplo: "Incendio" y "incendio" deberían ser el mismo grupo.
    Map<String, List<ColeccionHecho>> agrupadosPorTitulo = itemsColeccion.stream()
        .collect(Collectors.groupingBy(item -> item.getHecho().getTitulo().trim().toLowerCase()));

    agrupadosPorTitulo.forEach((tituloKey, grupo) -> {
      String tituloReal = grupo.get(0).getHecho().getTitulo();

      // A. Validar Cantidad Mínima de Registros
      if (grupo.size() < 2) {
        System.out.println("❌ [" + tituloReal + "] RECHAZADO: Solo hay " + grupo.size() + " mención/es (Mínimo 2).");
        grupo.forEach(item -> item.setEsConsensuado(false));
        return;
      }

      // B. Validar Fuentes Distintas
      long fuentesDistintas = grupo.stream()
          .map(item -> item.getHecho().getOrigen().getNombre())
          .distinct()
          .count();

      if (fuentesDistintas < 2) {
        System.out.println("❌ [" + tituloReal + "] RECHAZADO: Hay menciones repetidas de la misma fuente (Fuentes distintas: " + fuentesDistintas + ").");
        grupo.forEach(item -> item.setEsConsensuado(false));
        return;
      }

      // C. Validar Coherencia (Conflictos)
      Hecho referencia = grupo.get(0).getHecho();
      boolean hayConflicto = false;

      for (ColeccionHecho item : grupo) {
        Hecho actual = item.getHecho();
        if (!actual.esIgual(referencia)) {
          hayConflicto = true;
          System.out.println("⚠️ CONFLICTO DETECTADO en [" + tituloReal + "]:");
          System.out.println("   - Referencia: " + referencia.getDescripcion() + " | " + referencia.getFechaAcontecimiento());
          System.out.println("   - Diferente:  " + actual.getDescripcion() + " | " + actual.getFechaAcontecimiento());
          break;
        }
      }

      if (hayConflicto) {
        System.out.println("❌ [" + tituloReal + "] RECHAZADO: Las fuentes se contradicen (contenido distinto).");
        grupo.forEach(item -> item.setEsConsensuado(false));
      } else {
        System.out.println("✅ [" + tituloReal + "] APROBADO: " + fuentesDistintas + " fuentes coinciden sin conflictos.");
        grupo.forEach(item -> item.setEsConsensuado(true));
      }
    });
  }
}