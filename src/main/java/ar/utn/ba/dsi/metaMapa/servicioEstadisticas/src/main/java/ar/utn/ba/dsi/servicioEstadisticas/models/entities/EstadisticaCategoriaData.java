package ar.utn.ba.dsi.servicioEstadisticas.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estadistica_categoria_data")
public class EstadisticaCategoriaData {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "categoria", nullable = false)
  private String categoria;

  @Column(name = "cantidad_hechos", nullable = false)
  private Long cantidadHechos;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "estadistica_por_categoria_id", nullable = false)
  private EstadisticaPorCategoria estadisticaPorCategoria;
}
