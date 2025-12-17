package ar.utn.ba.dsi.servicioAgregador.services;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.ColeccionEditarInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.ColeccionInputManual_DTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;

import java.util.List;

public interface IColeccionService {

  List<ColeccionOutputDTO> buscarTodas();
  ColeccionOutputDTO buscarPorId(String id, String instanc);
  public ColeccionOutputDTO crear(ColeccionEditarInputDTO coleccionInputManual_dto);
  public List<HechoAgregadorOutputDTO> devolverHechosDeColeccion(String id, String instanciaUrl);
  void eliminar(String id);
  void eliminarHechoDeColecciones(Hecho hecho);
}
