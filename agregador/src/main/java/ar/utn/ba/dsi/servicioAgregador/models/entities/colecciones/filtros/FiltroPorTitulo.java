package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class FiltroPorTitulo implements Filtros {

  private String busqueda;

  public FiltroPorTitulo(String busqueda) {
    this.busqueda = busqueda;
  }

  @Override
  public boolean filtrar(Hecho hecho) {
    if (hecho.getTitulo() == null) return false;

    String tituloHecho = normalizarTexto(hecho.getTitulo());
    String textoBusqueda = normalizarTexto(this.busqueda);

    return tituloHecho.contains(textoBusqueda);
  }

  @Override
  public String getTipoFiltro() {
    return "TITULO";
  }

  @Override
  public String getValorFiltro() {
    return this.busqueda;
  }

  private String normalizarTexto(String input) {
    if (input == null) return "";
    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(normalized).replaceAll("").toLowerCase();
  }
}