package ar.utn.ba.dsi.servicioEstadisticas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServicioEstadisticasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioEstadisticasApplication.class, args);
	}

}
