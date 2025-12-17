package ar.utn.ba.dsi.metaMapa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MetaMapaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetaMapaApplication.class, args);
	}

}
