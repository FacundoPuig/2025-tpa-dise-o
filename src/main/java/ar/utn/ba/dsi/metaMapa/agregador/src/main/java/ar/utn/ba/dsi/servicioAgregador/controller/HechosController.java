package ar.utn.ba.dsi.servicioAgregador.controller;


import ar.utn.ba.dsi.servicioAgregador.models.dtos.ApiResponse;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.services.IHechoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/hechos")
@CrossOrigin(origins = "${frontend.client.url}")
public class HechosController {

  @Autowired
  private IHechoService hechoService;

  // Para usuarios autenticados y anónimos
  // ============================= ESTO DEBERIA HACERSE EN DINAMICA===================


  /*@GetMapping("")
  public ResponseEntity<List<HechoAgregadorOutputDTO>> obtenerHechos(@RequestParam(required = false) Map<String, String> filtros) {
    List<HechoAgregadorOutputDTO> hechos = hechoService.obtenerHechos(filtros);
    return ResponseEntity.ok(hechos);
  }*/

//  @GetMapping("/{titulo}")
//  public ResponseEntity<HechoAgregadorOutputDTO> obtenerUnHecho (@PathVariable String titulo) {
//    HechoAgregadorOutputDTO hechoAgregadorOutputDTO = hechoService.obtenerHechoPorTitulo(titulo);
//    return ResponseEntity.ok(hechoAgregadorOutputDTO);
//}

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<HechoAgregadorOutputDTO>> obtenerUnHechoPorId (@PathVariable("id") Long id) {
    HechoAgregadorOutputDTO hechoAgregadorOutputDTO = hechoService.obtenerHechoPorId(id);
    return ResponseEntity.ok()
        .body(new ApiResponse<>(200, "OK", "Detalle de hecho", hechoAgregadorOutputDTO));
  }

  // Para el dropdown del front
  @GetMapping("/categorias")
  public ResponseEntity<List<String>> getCategorias() {
    return ResponseEntity.ok(hechoService.listarCategorias());
  }

  @GetMapping("/usuario/{userId}")
  public ResponseEntity<List<HechoAgregadorOutputDTO>> obtenerHechosPorUsuario(@PathVariable String userId) {
    return ResponseEntity.ok(hechoService.buscarHechosPorUsuario(userId));
  }

  @PutMapping("/{name}/ocultar")
  public ResponseEntity<Void> ocultarHecho(@PathVariable("name") String hechoName) {
    hechoService.ocultarHecho(hechoName);
    System.out.println("Hecho ocultado con existe: " + hechoName);
    return ResponseEntity.noContent().build();
  }
}
