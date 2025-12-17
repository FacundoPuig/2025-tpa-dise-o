package ar.utn.ba.dsi.fuenteDinamica.models.entities.hechos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "origen")
public class Origenes {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "nombre", nullable = false, length = 100, unique = true)
  private String nombre;

  @Column(name = "proviene_de", nullable = false, length = 50)
  private Origen provieneDe; //dinamica se toma el id (hay que pasarlo a String), estatica se toma el path, proxy la url

  public Origenes(String nombre) {
    this.nombre = nombre; // id del visualizador que creo el hecho
    this.provieneDe = Origen.DINAMICA;
  }
}
