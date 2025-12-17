package ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.fuentes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="fuente")
public class Fuente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="nombreFuente", nullable=false)
	private String nombreFuente;

	@Column(name="url", nullable=false)
	private String url; //todo usar enum de origenn

	public Fuente(String nombreFuente, String url) {
		this.nombreFuente = nombreFuente;
		this.url = url;
	}
}
