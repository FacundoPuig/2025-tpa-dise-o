package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import ar.utn.ba.dsi.servicioAgregador.models.entities.intermedia.ColeccionHecho;
import java.util.List;


public interface Algoritmos {
  public void cumpleConConcenso(List<ColeccionHecho> listaDeColeccionHechos);
}
