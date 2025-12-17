package ar.utn.ba.dsi.servicioAgregador.models.entities.intermedia;

import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.Coleccion;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Hecho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "coleccion_hecho")
@Getter @Setter @NoArgsConstructor
public class ColeccionHecho {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Relación N:1 con Colección (Tu lado "Dueño")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coleccion_handle_id", nullable = false) // FK a Coleccion.handleID
  private Coleccion coleccion;

  // Relación N:1 con Hecho (El lado "Pasivo")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hecho_id", nullable = false) // FK a Hecho.id
  private Hecho hecho;

  // Tu atributo extra
  @Column(name = "es_consensuado")
  private boolean esConsensuado;

  public ColeccionHecho(Coleccion coleccion, Hecho hecho) {
    this.coleccion = coleccion;
    this.hecho = hecho;
    this.esConsensuado = false;
  }

}