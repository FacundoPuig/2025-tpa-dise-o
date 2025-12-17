package ar.utn.ba.dsi.servicioAgregador.models.entities.hechos;

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
  private String nombre;

  @Column(name = "provieneDe", nullable = false, length = 50)
  private Origen provieneDe; //dinamica se toma el id (hay que pasarlo a String), estatica se toma el path, proxy la url

  public Origenes(String nombre, Origen provieneDe) {
    this.nombre = nombre;
    this.provieneDe = provieneDe;
  }
}

//TODO: ver de hacer una clase abstracta para el origen, para poder conectar el id con el visualizador en la tabla
// y no duplicar datos