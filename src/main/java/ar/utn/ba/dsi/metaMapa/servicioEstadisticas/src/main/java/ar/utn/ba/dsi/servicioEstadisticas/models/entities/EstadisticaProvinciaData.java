package ar.utn.ba.dsi.servicioEstadisticas.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estadistica_provincia_data")
public class EstadisticaProvinciaData {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "provincia", nullable = false)
  private String provincia;

  @Column(name = "cantidad_hechos", nullable = false)
  private Long cantidadHechos;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "estadistica_por_provincia_id", nullable = false)
  private EstadisticaPorProvincia estadisticaPorProvincia;
}
