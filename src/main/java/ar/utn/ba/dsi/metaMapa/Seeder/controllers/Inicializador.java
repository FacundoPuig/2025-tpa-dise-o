package ar.utn.ba.dsi.metaMapa.Seeder.controllers;

import ar.utn.ba.dsi.metaMapa.Seeder.ISeederService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iniciar")
@CrossOrigin("http://localhost:8080")
public class Inicializador {

  @Autowired
  private ISeederService seederService;

  @GetMapping("")
  public void inicializar() {
    this.seederService.init();
  }
}
