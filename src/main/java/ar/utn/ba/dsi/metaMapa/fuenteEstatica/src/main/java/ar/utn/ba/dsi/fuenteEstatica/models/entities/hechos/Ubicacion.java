package ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ubicacion")
public class Ubicacion {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "latitud", nullable = false)
  private Double latitud;

  @Column(name = "longitud", nullable = false)
  private Double longitud;



  public Ubicacion(Double latitud, Double longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }
}
