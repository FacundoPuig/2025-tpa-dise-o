package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.filtros;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class FiltrosConverter implements AttributeConverter<List<Filtros>, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final FiltrosFactory filtroFactory = new FiltrosFactory();

  @Override
  public String convertToDatabaseColumn(List<Filtros> filtros) {
    // Convierte List<Filtros> a un String JSON
    if (filtros == null || filtros.isEmpty()) {
      return null;
    }
    try {
      //Convertimos cada objeto Filtro a un mapa simple
      List<Map<String, String>> listaParaJson = filtros.stream()
          .map(filtro -> Map.of(
              "tipo", filtro.getTipoFiltro(),
              "valor", filtro.getValorFiltro()
          ))
          .collect(Collectors.toList());

      //Convertimos la lista de mapas a un string JSON
      return objectMapper.writeValueAsString(listaParaJson);

    } catch (Exception e) {
      throw new RuntimeException("Error al convertir filtros a JSON", e);
    }
  }

  @Override
  public List<Filtros> convertToEntityAttribute(String dbData) {
    // Convierte el String JSON de la BD a una List<Filtros>
    if (dbData == null || dbData.isBlank()) {
      return new ArrayList<>();
    }
    try {
      //Leemos el JSON a una lista de mapas
      TypeReference<List<Map<String, String>>> typeRef = new TypeReference<>() {};
      List<Map<String, String>> listaDesdeJson = objectMapper.readValue(dbData, typeRef);

      //Usamos el Factory para reconstruir cada objeto Filtro
      return listaDesdeJson.stream()
          .map(mapa -> filtroFactory.crearInstancia(mapa.get("tipo"), mapa.get("valor")))
          .collect(Collectors.toList());

    } catch (Exception e) {
      throw new RuntimeException("Error al convertir JSON a filtros", e);
    }
  }
}
