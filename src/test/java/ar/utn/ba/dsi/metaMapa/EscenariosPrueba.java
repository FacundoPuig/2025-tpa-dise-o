package ar.utn.ba.dsi.metaMapa;



//------------------------- ESCENARIO 1 ----------------------------------------------

/*
public class EscenariosPrueba {

    Usuario usuario = new Usuario();
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

    List<Hecho> hechos = List.of(hecho1, hecho2, hecho3, hecho4, hecho5);

   Coleccion coleccionDePrueba = new Coleccion("Coleccion de prueba", "Esto es una prueba", hechos);

   @Test
  public void obtenerHechosDeLaColeccion() {
    List<Hecho> hechosColeccion = coleccionDePrueba.getHechos();
    assertEquals(hechos.size(), hechosColeccion.size());
  }

  @Test
  public void criterioDePertenenciaPorFecha() {
    FiltroPorFecha criterioFechas = new FiltroPorFecha(LocalDate.of(2000, 1, 1), LocalDate.of(2010, 1, 1));
    coleccionDePrueba.agregarCriterioPertenencia(criterioFechas);
    assertEquals(3, coleccionDePrueba.getHechos().size());
  }

  @Test
  public void criterioDePertenenciaPorCategoria() {
    FiltroPorCategoria criterioCategoria = new FiltroPorCategoria("Caída de aeronave");
    coleccionDePrueba.agregarCriterioPertenencia(criterioCategoria);
    assertEquals(2, coleccionDePrueba.getHechos().size());
  }

  @Test
  public void criterioDePertenenciaPorUbicacion() {
    FiltroPorUbicacion criterioUbicacion = new FiltroPorUbicacion("-33.768051-61.92103");
    coleccionDePrueba.agregarCriterioPertenencia(criterioUbicacion);
    assertEquals(1, coleccionDePrueba.getHechos().size());
  }

  @Test
  public void filtrarColeccionPorCategoria() {

    List<Filtros> filtros = Arrays.asList(
        new FiltroPorCategoria("Caída de Aeronave"),
        new FiltroPorTitulo("un título")
    );

    assertEquals(0, coleccionDePrueba.recorrer(filtros).size());
  }

  @Test
  public void agregarEtiquetasAUnHecho() {
    hecho1.agregarEtiqueta("Olavarría");
    hecho1.agregarEtiqueta("Grave");

    String etiqueta1 = hecho1.getEtiquetas().get(0);
    String etiqueta2 = hecho1.getEtiquetas().get(1);

    assertEquals("Olavarría", etiqueta1);
    assertEquals("Grave", etiqueta2);
  }

  //------------------------- ESCENARIO 2 ----------------------------------------------

  @Test
  public void importarHechosPorCSV() {
     Coleccion coleccion = new Coleccion();
     String path = "src/";
     Estatica estatica = new Estatica(path);
     estatica.agregarHechosAColeccion(coleccion);
     assertEquals(15000, coleccion.getHechos().size()); //lista filtrada

    Hecho primero = coleccion.getHechos().get(0);
    assertEquals("Ráfagas de más de 100 km/h causa estragos en San Vicente, Misiones", primero.getTitulo());
  }

//------------------------- ESCENARIO 3 ----------------------------------------------
    Visualizador contribuyente = new Visualizador("Camila", "Tobares", 23);
    Visualizador administrador = new Visualizador("Uriel", "Corrales", 25);

    String motivoLargo = "Solicito la eliminación de este hecho porque se detectaron errores en la información cargada. Al revisar los datos, notamos que hay inconsistencias con respecto a otras fuentes y registros oficiales. Además, este hecho parece estar duplicado o mal categorizado, lo cual puede generar confusión y afectar la calidad general de la base de datos. A mi parecer, es mejor eliminarlo para evitar problemas y, si corresponde, volver a ingresarlo con la información correcta y validada. La idea es mantener el orden y asegurar que todo lo que figure esté bien respaldado.";

  @Test
  public void verificarSolicitudDeEliminacionRechazada() {
    contribuyente.solicitarEliminacion(hecho6, motivoLargo);
    SolicitudEliminacion solicitud1 = administrador.getSolicitudesPendientes().get(0);

    // Modificar la fecha de creación y rechazarla al día siguiente
    LocalDateTime fechaCreacion = LocalDateTime.of(2025, 4, 20, 14, 30);
    LocalDateTime fechaModificacion = fechaCreacion.plusDays(1);

    solicitud1.setFechaCreacionSolicitud(fechaCreacion);
    solicitud1.getRegistroCambioEstado().get(0).setFechaModificacion(fechaCreacion);

    solicitud1.setFechaUltimaModificacionSolicitud(fechaModificacion);
    administrador.rechazarSolicitud(solicitud1);
    solicitud1.getRegistroCambioEstado().get(solicitud1.getRegistroCambioEstado().size()-1).setFechaModificacion(fechaModificacion);

    assertEquals(0, administrador.getSolicitudesPendientes().size());
    assertEquals(1, administrador.getSolicitudesRespondidas().size());
    assertEquals("Rechazada", administrador.getSolicitudesRespondidas().get(0).getEstado().Estado());
    assertEquals(fechaCreacion, administrador.getSolicitudesRespondidas().get(0).getFechaCreacionSolicitud());
    assertEquals(fechaModificacion, administrador.getSolicitudesRespondidas().get(0).getFechaUltimaModificacionSolicitud());

  }

  //---------------------- SEGUNDA SOLICITUD ---------------------------------------

  @Test
  public void verificarSolicitudAceptada(){
    contribuyente.solicitarEliminacion(hecho6, motivoLargo);
    SolicitudEliminacion solicitud2 = administrador.getSolicitudesPendientes().get(0);

    // Modificar fecha creación y aceptar solicitud a las 2hs
    LocalDateTime nuevaCreacion = LocalDateTime.of(2025, 4, 22, 10, 0);
    LocalDateTime nuevaModificacion = nuevaCreacion.plusHours(2);

    solicitud2.setFechaCreacionSolicitud(nuevaCreacion);

    administrador.aceptarSolicitud(solicitud2);
    solicitud2.setFechaUltimaModificacionSolicitud(nuevaModificacion);

    assertEquals(0, administrador.getSolicitudesPendientes().size());
    assertEquals(2, administrador.getSolicitudesRespondidas().size()); // si se corre el test indivualmente va a tirar error porque espera 1 solicitud respondida y no 2. Si se corre todos los test a la vez, va a tirar bien.
    assertEquals("Aceptada", administrador.getSolicitudesRespondidas().get(administrador.getSolicitudesRespondidas().size() - 1).getEstado().Estado());
    assertEquals(nuevaCreacion, administrador.getSolicitudesRespondidas().get(administrador.getSolicitudesRespondidas().size() - 1).getFechaCreacionSolicitud());
    assertEquals(nuevaModificacion, administrador.getSolicitudesRespondidas().get(administrador.getSolicitudesRespondidas().size() - 1).getFechaUltimaModificacionSolicitud());

  }

}*/
