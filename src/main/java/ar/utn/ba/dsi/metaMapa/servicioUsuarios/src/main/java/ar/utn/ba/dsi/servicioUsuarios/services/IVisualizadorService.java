package ar.utn.ba.dsi.servicioUsuarios.services;


import ar.utn.ba.dsi.servicioUsuarios.models.dtos.input.VisualizadorInputDTO;
import ar.utn.ba.dsi.servicioUsuarios.models.dtos.output.VisualizadorOutputDTO;
import java.util.List;

public interface IVisualizadorService {
  VisualizadorOutputDTO crear(VisualizadorInputDTO visualizadorInput);
  List<VisualizadorOutputDTO> buscarTodas();
  VisualizadorOutputDTO actualizarPerfil(String email, VisualizadorInputDTO visualizadorInput) throws Exception;
}
