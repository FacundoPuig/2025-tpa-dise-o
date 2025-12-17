package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.algoritmosConsenso;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // Lo hace automaticamente en cualquier entidad cuando encuentra un campo de tipo de dato Algoritmo
public class AlgoritmosConverter implements AttributeConverter<Algoritmos, String> {

  private final AlgoritmosFactory factory = new AlgoritmosFactory();

  @Override
  public String convertToDatabaseColumn(Algoritmos algoritmo) {
    // Convierte el objeto a un String para guardarlo en la DB
    if (algoritmo == null) {
      return null;
    }
    if (algoritmo instanceof Absoluta) {
      return "MAYORIA_ABSOLUTA";
    }
    if (algoritmo instanceof MayoriaSimple) {
      return "MAYORIA_SIMPLE";
    }
    if (algoritmo instanceof MultiplesMenciones) {
      return "MULTIPLES_MENCIONES";
    }

    throw new IllegalArgumentException("Tipo de algoritmo desconocido para convertir a columna de DB");
  }

  @Override
  public Algoritmos convertToEntityAttribute(String dbData) {
    // Convierte el String de la DB de vuelta al objeto usando el Factory
    if (dbData == null || dbData.isBlank()) {
      return null;
    }
    return factory.crearInstancia(dbData);
  }
}
