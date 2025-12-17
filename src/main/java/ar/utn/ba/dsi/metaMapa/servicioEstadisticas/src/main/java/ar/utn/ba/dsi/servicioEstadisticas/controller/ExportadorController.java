package ar.utn.ba.dsi.servicioEstadisticas.controller;

import ar.utn.ba.dsi.servicioEstadisticas.services.ExportadorCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/exportar")
public class ExportadorController {

  @Autowired
  private ExportadorCSV exportadorCSV;

  @GetMapping("/csv/provincia-por-coleccion")
  public ResponseEntity<String> exportarEstadisticaPorProvinciaACsv() {
    String csvContent = exportadorCSV.exportarEstadisticaPorProvinciaACsv();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Historial_Provincia_Coleccion.csv\"")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(csvContent);
  }

  @GetMapping("/csv/categoria-mas-hechos")
  public ResponseEntity<String> exportarEstadisticaPorCategoriaACsv() {
    String csvContent = exportadorCSV.exportarEstadisticaPorCategoriaACsv();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Historial_Categoria_Global.csv\"")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(csvContent);
  }

  @GetMapping("/csv/provincia-por-categoria")
  public ResponseEntity<String> exportarEstadisticaPorProvinciaYCategoriaACsv() {
    String csvContent = exportadorCSV.exportarEstadisticaPorProvinciaYCategoriaACsv();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Historial_Provincia_Por_Categoria.csv\"")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(csvContent);
  }

  @GetMapping("/csv/hora-por-categoria")
  public ResponseEntity<String> exportarEstadisticaPorHoraYCategoriaACsv() {
    String csvContent = exportadorCSV.exportarEstadisticaPorHoraYCategoriaACsv();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Historial_Hora_Por_Categoria.csv\"")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(csvContent);
  }

  @GetMapping("/csv/spam")
  public ResponseEntity<String> exportarEstadisticaDeSpamACsv() {
    String csvContent = exportadorCSV.exportarEstadisticaDeSpamACsv();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Historial_Solicitudes_Spam.csv\"")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(csvContent);
  }

  @GetMapping("/zip/todas")
  public ResponseEntity<byte[]> exportarTodasLasEstadisticasAZip() {
    try {
      byte[] zipBytes = exportadorCSV.exportarTodasLasEstadisticasAZip();
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Historial_Total_Estadisticas.zip\"")
          .contentType(MediaType.parseMediaType("application/zip"))
          .body(zipBytes);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null);
    }
  }
}