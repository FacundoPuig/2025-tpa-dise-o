package ar.utn.ba.dsi.servicioUsuarios.models.entities;

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

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "UsuarioFisico")
public class UsuarioFisico {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "apellido")
  private String apellido;

  @Column(name = "fechaDeNacimiento")
  private LocalDate fechaDeNacimiento;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "hashContrasenia")
  private String hashContrasenia;

  public UsuarioFisico(String nombre, String apellido, LocalDate fechaDeNacimiento, String email, String hashContrasenia) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.fechaDeNacimiento = fechaDeNacimiento;
    this.email = email;
    this.hashContrasenia = hashContrasenia;
  }

}
