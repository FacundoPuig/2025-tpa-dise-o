package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;

public class FiltroPorUbicacion implements Filtros {

  private String busqueda;

  public FiltroPorUbicacion(String busqueda){
    this.busqueda = busqueda;
  }

  @Override
  public boolean filtrar(Hecho hecho){
    // 1. Validaciones de seguridad en cadena
    if (hecho.getUbicacion() == null || hecho.getUbicacion().getProvincia() == null) {
      return false;
    }

    // 2. Usar equalsIgnoreCase para que no importen las mayúsculas/minúsculas
    // O puedes usar contains() si quieres búsqueda parcial (ej: "Santiago" encuentra "Santiago del Estero")
    return hecho.getUbicacion().getProvincia().toLowerCase().contains(busqueda.toLowerCase());
  }

  @Override
  public String getTipoFiltro() {
    return "UBICACION";
  }

  @Override
  public String getValorFiltro() {
    return this.busqueda;
  }
}