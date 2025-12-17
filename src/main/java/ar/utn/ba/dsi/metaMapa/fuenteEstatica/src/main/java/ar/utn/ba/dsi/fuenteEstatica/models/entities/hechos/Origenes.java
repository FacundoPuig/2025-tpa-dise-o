package ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos;

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
@Entity
@Table(name = "origen")
public class Origenes {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "nombre", nullable = false, length = 200)
  private String nombre;  //estatica se toma el path del csv

  @Column(name = "provieneDe", nullable = false, length = 50)
  private Origen provieneDe;

  public Origenes(String nombre) {
    this.nombre = nombre;
    this.provieneDe = Origen.ESTATICA;
  }
}
