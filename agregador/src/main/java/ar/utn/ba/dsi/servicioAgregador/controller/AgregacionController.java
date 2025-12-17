package ar.utn.ba.dsi.servicioAgregador.controller;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.services.impl.ServicioAgregacion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agregacion")
@CrossOrigin(origins = "${frontend.client.url}")
public class AgregacionController {

  @Autowired
  private ServicioAgregacion servicioAgregacion;


  //TODO ES DE PRUEBA, NO TIENE QUE USARSE EN EL SISTEMA FINAL
  @GetMapping("/actualizar")
  public void obtenerHechosDeColeccion() {
     servicioAgregacion.refrescarHechosPendientes();
  }

  @GetMapping("/actualiza-consenso")
  public void actualizarConsenso() {
     servicioAgregacion.ejecutarAlgoritmoDeConsenso();
     }
}