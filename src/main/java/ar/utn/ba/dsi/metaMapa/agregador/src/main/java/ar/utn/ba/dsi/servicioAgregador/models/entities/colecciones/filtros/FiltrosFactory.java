package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FiltrosFactory {

  public Filtros crearInstancia(String tipo, String valor) {
    if (tipo == null || tipo.isBlank()) throw new IllegalArgumentException("Tipo nulo");

    switch (tipo.toUpperCase()) {
      case "UBICACION": return new FiltroPorUbicacion(valor);
      case "TITULO":    return new FiltroPorTitulo(valor);
      case "CATEGORIA": return new FiltroPorCategoria(valor);
      case "FECHA":
        String[] fechas = valor.split("\\|");
        if (fechas.length != 2) {
          throw new IllegalArgumentException("Formato de fecha inválido para el filtro: " + valor);
        }

        String inicioStr = fechas[0];
        String finStr = fechas[1];

        // Si viene "2025-11-05T00:00" (16 caracteres), le agregamos ":00"
        if (inicioStr.length() == 16) inicioStr += ":00";
        if (finStr.length() == 16) finStr += ":00";

        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fin = LocalDateTime.parse(finStr);

        return new FiltroPorFecha(inicio, fin);
      default: throw new IllegalArgumentException("Filtro desconocido: " + tipo);
    }
  }

  public List<Filtros> crearDesdeParametros(Map<String, String> parametros) {
    List<Filtros> filtros = new ArrayList<>();

    String pInicio = parametros.get("fechaInicio");
    String pFin = parametros.get("fechaFin");

    if ((pInicio != null && !pInicio.isBlank()) || (pFin != null && !pFin.isBlank())) {
      LocalDateTime inicio = parseFlexible(pInicio, true);
      LocalDateTime fin = parseFlexible(pFin, false);

      if (inicio != null || fin != null) {
        filtros.add(new FiltroPorFecha(inicio, fin));
      } else {
        System.err.println("-> ERROR: No se pudo crear el filtro de fecha (Parseo falló en ambos).");
      }
    }

    if (hayValor(parametros, "titulo")) filtros.add(new FiltroPorTitulo(parametros.get("titulo")));
    if (hayValor(parametros, "categoria")) filtros.add(new FiltroPorCategoria(parametros.get("categoria")));
    if (hayValor(parametros, "ubicacion")) filtros.add(new FiltroPorUbicacion(parametros.get("ubicacion")));

    return filtros;
  }

  private boolean hayValor(Map<String, String> map, String key) {
    return map.containsKey(key) && !map.get(key).isBlank();
  }

  // --- PARSEO ROBUSTO CON DEBUG ---
  private LocalDateTime parseFlexible(String dateStr, boolean esInicio) {
    if (dateStr == null || dateStr.isBlank() || "null".equals(dateStr)) return null;

    dateStr = dateStr.trim();

    try {
      // 1. Intento ISO Time (2024-01-01T10:00:00)
      return LocalDateTime.parse(dateStr);
    } catch (Exception e1) {
      try {
        // 2. Intento ISO Date (2024-01-01) -> Típico de input type="date"
        return completarHora(LocalDate.parse(dateStr), esInicio);
      } catch (Exception e2) {
        try {
          // 3. Intento Latam (01/01/2024)
          DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          return completarHora(LocalDate.parse(dateStr, fmt), esInicio);
        } catch (Exception e3) {
          try {
            // 4. Intento Latam guiones (01-01-2024)
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return completarHora(LocalDate.parse(dateStr, fmt), esInicio);
          } catch (Exception e4) {
            System.err.println("❌ FALLÓ PARSEO DE FECHA: " + dateStr);
            return null;
          }
        }
      }
    }
  }

  private LocalDateTime completarHora(LocalDate date, boolean esInicio) {
    return esInicio ? date.atStartOfDay() : date.atTime(LocalTime.MAX);
  }
}