package ar.utn.ba.dsi.fuenteEstatica.models.entities.solicitudes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "registro_cambio_estado")
public class RegistroCambioEstado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  @Enumerated(EnumType.STRING)
  private Estados estado;

  @Column(name = "fecha_modificacion", nullable = false)
  private LocalDateTime fechaModificacion = LocalDateTime.now();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "solicitud_eliminacion_id")
  private SolicitudEliminacion solicitudEliminacion;
  //si la lista es static, hay que poder diferenciar de que solicitud se habla, pues se mezclan. Ademas, una vez rechazada
  // o aceptada la solicitud, se descarta/elimina la solicitud. Esta lista es para llevar registro de la eliminacion, aceptacion y pedido de
  // solicitudes.
  @Column(name = "motivo_rechazo", length = 255)
  private String motivoRechazo;
  //ver de agregar motivo de rechazo

  public RegistroCambioEstado(Estados estado, SolicitudEliminacion solicitud) {
    this.estado = estado;
    this.solicitudEliminacion = solicitud;
    //this.motivoRechazo = motivo;
  }
}
