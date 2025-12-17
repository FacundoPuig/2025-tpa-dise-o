package ar.utn.ba.dsi.fuenteEstatica.models.entities.hechos;

import jakarta.persistence.*;
import lombok.*; // Dejar lombok por si acaso, pero agregamos el método manual
import java.util.List;

@Getter
@Setter
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

  public String getNombre() {
    return this.nombre;
  }
}