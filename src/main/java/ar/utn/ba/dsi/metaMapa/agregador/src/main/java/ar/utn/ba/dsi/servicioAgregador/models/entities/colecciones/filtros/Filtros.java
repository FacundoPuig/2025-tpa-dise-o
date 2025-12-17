package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;

public interface Filtros {
 boolean filtrar(Hecho hecho);
 String getTipoFiltro();
 String getValorFiltro();
}
