package ar.utn.ba.dsi.fuenteEstatica.controller;

import ar.utn.ba.dsi.fuenteEstatica.models.dtos.output.HechoEstaticaOutputDTO;
import ar.utn.ba.dsi.fuenteEstatica.models.entities.solicitudes.SolicitudEliminacion;
import ar.utn.ba.dsi.fuenteEstatica.services.IEstaticaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "${frontend.client.url}")
@RequestMapping("/estatica")
public class EstaticaController {

  @Autowired
  private IEstaticaService estaticaService;

  //interno al codigo. Lo va a usar el agregador para obtener los hechos
  @GetMapping("/hechos")
  public List<HechoEstaticaOutputDTO> Hechos() {
    return estaticaService.enviarHechosAAgregador();
  }

  @PostMapping("/cargar-csv")
  public void cargarCSV(@RequestParam("archivoCSV") MultipartFile archivoCSV) {
    estaticaService.cargarCSV(archivoCSV);
  }

//  //endpint paara recibir y guardar solicitudes de eliminacion de hechos
//  @PostMapping("/hechos/eliminar")
//  public void solicitudEliminacion(@RequestParam("solicitud") SolicitudEliminacion solicitudEliminacion) {
//    // Lógica para manejar la solicitud de eliminación
//    System.out.println("Solicitud de eliminación recibida para el hecho ID: " + solicitudEliminacion);
//    estaticaService.ocultarHecho(solicitudEliminacion.getUnHecho().getId());
//
//  }

  @PutMapping("/hechos/{titulo}/ocultar")
  public void ocultarHecho(@PathVariable("titulo") String titulo) {
    System.out.println("Solicitud de eliminación recibida para el hecho ID: " + titulo);
    estaticaService.ocultarHecho(titulo);
    System.out.println("Hecho con ID " + titulo + " ha sido ocultado.");
  }
}