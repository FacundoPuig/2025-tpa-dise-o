package ar.utn.ba.dsi.servicioAgregador.models.entities.usuarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table(name = "usuario_fisico")
public class UsuarioFisico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="nombre", nullable=false, length=100)
	private String nombre;

	@Column(name="apellido", nullable=false, length=100)
	private String apellido;

	@Column(name="fechaDeNacimiento", nullable=false)
	private LocalDate fechaDeNacimiento;

	@Column(name="email", nullable=false, length=200)
	private String email;

	@Column(name="hashContrasenia", nullable=false, length=100)
	private String hashContrasenia;

	public UsuarioFisico() {

	}
}