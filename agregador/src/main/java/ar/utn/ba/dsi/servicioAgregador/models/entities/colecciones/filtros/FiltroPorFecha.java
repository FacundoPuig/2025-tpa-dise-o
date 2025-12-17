package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros;

import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import java.time.LocalDateTime;

public class FiltroPorFecha implements Filtros {
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaFin;

  public FiltroPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    this.fechaInicio = fechaInicio;
    this.fechaFin = fechaFin;
  }

  @Override
  public boolean filtrar(Hecho hecho) {
    LocalDateTime fechaHecho = hecho.getFechaAcontecimiento();
    if (fechaHecho == null) return false;

    boolean pasaInicio = (fechaInicio == null) || !fechaHecho.isBefore(fechaInicio);
    boolean pasaFin = (fechaFin == null) || !fechaHecho.isAfter(fechaFin);

    return pasaInicio && pasaFin;
  }

  @Override
  public String getTipoFiltro() { return "FECHA"; }

  @Override
  public String getValorFiltro() {
    return (fechaInicio != null ? fechaInicio : "") + "|" + (fechaFin != null ? fechaFin : "");
  }
}