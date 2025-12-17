/*
package ar.utn.ba.dsi.metaMapa.Seeder.impl;

import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Categoria;
import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Ubicacion;
import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos.Origenes;
import ar.utn.ba.dsi.metaMapa.agregador.src.main.java.ar.utn.ba.dsi.servicioAgregador.models.entities.repositories.IColeccionRepository;
import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.entities.usuarios.UsuarioFisico;
import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.entities.usuarios.Visualizador;
import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.repositories.IHechoDinamicaRepository;
import ar.utn.ba.dsi.metaMapa.fuenteDinamica.src.main.java.ar.utn.ba.dsi.fuenteDinamica.models.repositories.IVisualizadorRepository;
import ar.utn.ba.dsi.metaMapa.Seeder.ISeederService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SeederService implements ISeederService {
  @Autowired
  private IVisualizadorRepository visualizadorRepository;
  @Autowired
  private IColeccionRepository coleccionRepository;
  @Autowired
  private IHechoDinamicaRepository hechoDinamicaRepository;



  public void init() {
    // carga del fileReader
    */
/*FileReader lector = new FileReader();
    lector.leerHechosDesdeCSV("");*//*


    Visualizador administrador = new Visualizador(new UsuarioFisico("Luis", "Ortega", LocalDate.of(1999, 7, 3), "luis@gmail.com","1234"),true);
    Visualizador contribuyente = new Visualizador(new UsuarioFisico("Pepe", "Argento", LocalDate.of(2002, 6, 10), "pepe@gmail.com", "pepeargento"), false);

    administrador.setUserId("uno");
    contribuyente.setUserId("dos");
    visualizadorRepository.save(administrador);
    visualizadorRepository.save(contribuyente);

    Origenes usuario = new Origenes("Usuario");
    Hecho hecho1 = new Hecho("Caida de aeronave imapcta en Olavarría",
        "Grave caída de aeronave ocurrió en las inmediaciones de Olavarría, Buenos Aires. El incidente provocó pánico entre los residentes locales. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.",
        new Categoria("Caída de aeronave"),
        LocalDate.of(2001, 11, 29),
        "",
        usuario,
        new Ubicacion(-36.868375,-60.343297)
    );

    Hecho hecho2 = new Hecho("Serio incidente: Accidente con maquinaria industrial en Chos Malal, Neuquén",
        "Un grave accidente con maquinaria industrial se registró en Chos Malal, Neuquén. El incidente dejó a varios sectores sin comunicación. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.",
        new Categoria("Accidente con maquinaria industrial"),
        LocalDate.of(2001, 8, 16),
        "",
        usuario,
        new Ubicacion(-37.345571, -70.241485)
    );
    Hecho hecho3 = new Hecho("Caída de aeronave impacta en Venado Tuerto, Santa Fe",
        "Grave caída de aeronave ocurrió en las inmediaciones de Venado Tuerto, Santa Fe. El incidente destruyó viviendas y dejó a familias evacuadas. Autoridades nacionales se han puesto a disposición para brindar asistencia.",
        new Categoria("Caída de aeronave"),
        LocalDate.of(2008, 8, 8),
        "",
        usuario,
        new Ubicacion (-33.768051, -61.92103)
    );

    Hecho hecho4 = new Hecho(
        "Accidente en paso a nivel deja múltiples daños en Pehuajó, Buenos Aires",
        "Grave accidente en paso a nivel ocurrió en las inmediaciones de Pehuajó, Buenos Aires. El incidente generó preocupación entre las autoridades provinciales. El Ministerio de Desarrollo Social está brindando apoyo a los damnificados.",
        new Categoria("Accidente en paso a nivel"),
        LocalDate.of(2020, 1, 27),
        "",
        usuario,
        new Ubicacion(-35.855811, -61.940589)
    );

    Hecho hecho5 = new Hecho("Devastador Derrumbe en obra en construcción afecta a Presidencia Roque Sáenz Peña",
        "Un grave derrumbe en obra en construcción se registró en Presidencia Roque Sáenz Peña, Chaco. El incidente generó preocupación entre las autoridades provinciales. El intendente local se ha trasladado al lugar para supervisar las operaciones.",
        new Categoria("Derrumbe en obra en construcción"),
        LocalDate.of(2016, 6, 4),
        "",
        usuario,
        new Ubicacion(-26.780008, -60.458782)
    );

    Hecho hecho6 = new Hecho(
        "Brote de enfermedad contagiosa causa estragos en San Lorenzo, Santa Fe ",
        "Grave brote de enfermedad contagiosa ocurrió en las inmediaciones de San Lorenzo, Santa Fe. El incidente dejó varios heridos y daños materiales. Se ha declarado estado de emergencia en la región para facilitar la asistencia.",
        new Categoria("Evento sanitario"),
        LocalDate.of(2005,07,05),
        "",
        usuario,
        new Ubicacion(-32.786098, -60.741543)
    );

    hechoDinamicaRepository.save(hecho1);
    hechoDinamicaRepository.save(hecho2);
    hechoDinamicaRepository.save(hecho3);
    hechoDinamicaRepository.save(hecho4);
    hechoDinamicaRepository.save(hecho5);
    hechoDinamicaRepository.save(hecho6);


  }

}
*/
