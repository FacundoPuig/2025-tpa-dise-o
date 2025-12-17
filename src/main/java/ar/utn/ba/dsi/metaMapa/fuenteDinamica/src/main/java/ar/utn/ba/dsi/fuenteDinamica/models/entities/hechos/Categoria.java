package ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Esto evita el error de Lazy Loading en el JSON
@Entity
@Table(name = "categoria")
public class Categoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "nombre", nullable = false, length = 255, unique = true)
  private String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }
}