package ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes;


public interface IDetectorDeSpam {
  public boolean esSpam(String titulo);
}
