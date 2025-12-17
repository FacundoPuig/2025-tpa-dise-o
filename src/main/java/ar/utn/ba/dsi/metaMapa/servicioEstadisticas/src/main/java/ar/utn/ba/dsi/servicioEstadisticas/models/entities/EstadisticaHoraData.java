package ar.utn.ba.dsi.servicioEstadisticas.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estadistica_hora_data")
public class EstadisticaHoraData {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "hora_del_dia", nullable = false)
  private Integer horaDelDia;

  @Column(name = "cantidad_hechos", nullable = false)
  private Long cantidadHechos;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "estadistica_por_hora_y_categoria_id", nullable = false)
  private EstadisticaPorHoraYCategoria estadisticaPorHoraYCategoria;
}
