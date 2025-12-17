package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.services.impl.NormalizacionService;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class FiltroPorCategoria implements Filtros {

  private String busqueda;

  public FiltroPorCategoria(String busqueda) {
    this.busqueda = busqueda;
  }

  @Override
  public boolean filtrar(Hecho hecho) {
    if (hecho.getCategoria() == null || hecho.getCategoria().getNombre() == null) return false;

    // 1. Normalizamos el nombre que viene del Hecho (ej: "Anegamiento masivo" -> "Inundación")
    String categoriaDelHecho = NormalizacionService.getCategoriaNormalizada(hecho.getCategoria().getNombre());

    // 2. Normalizamos lo que buscó el usuario (ej: "inundacion" -> "Inundación")
    String categoriaBuscada = NormalizacionService.getCategoriaNormalizada(this.busqueda);

    // 3. Limpiamos ambos para comparar sin tildes ni mayúsculas
    String limpioHecho = limpiar(categoriaDelHecho);
    String limpioBusqueda = limpiar(categoriaBuscada);

    // 4. Comparamos
    return limpioHecho.contains(limpioBusqueda);
  }

  private String limpiar(String s) {
    if (s == null) return "";
    String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    return t.toLowerCase().trim();
  }

  @Override
  public String getTipoFiltro() {
    return "CATEGORIA";
  }

  @Override
  public String getValorFiltro() {
    return this.busqueda;
  }
}