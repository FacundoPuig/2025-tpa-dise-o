package ar.utn.ba.dsi.servicioAgregador.models.entities.hechos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="etiqueta")
public class Etiqueta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "nombre", nullable = false, unique = true)
  private String nombre;

  @Column(name = "descripcion", nullable = false)
  private String descripcion;

  @ManyToMany(mappedBy = "etiquetas")
  private List<Hecho> hechosRelacionados;

  public Etiqueta(String nombre, String descripcion) {
    this.nombre = nombre;
    this.descripcion = descripcion;
  }
  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }
}
