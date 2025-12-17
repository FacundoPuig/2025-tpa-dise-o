package ar.utn.ba.dsi.servicioAgregador.services;

import ar.utn.ba.dsi.servicioAgregador.models.dtos.input.HechoAgregadorInputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.dtos.output.HechoAgregadorOutputDTO;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;

import java.util.List;

public interface IHechoService {
  public HechoAgregadorOutputDTO hechoAgregadorOutputDTO(Hecho hecho);
  List<HechoAgregadorOutputDTO> conseguirTodosLosHechos();
  HechoAgregadorOutputDTO obtenerHechoPorTitulo(String titulo);
  HechoAgregadorOutputDTO obtenerHechoPorId(Long id);
  List<String> listarCategorias();
  List<HechoAgregadorOutputDTO> buscarHechosPorUsuario(String userId);
  void guardarHechosMasivos(List<Hecho> hechos);
  void ocultarHecho(String hechoName);
}
