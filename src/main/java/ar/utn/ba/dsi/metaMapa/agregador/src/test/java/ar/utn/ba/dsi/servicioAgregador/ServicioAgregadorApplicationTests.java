package ar.utn.ba.dsi.servicioAgregador;//package ar.utn.ba.dsi.metaMapa.agregador.src.test.java.ar.utn.ba.dsi.servicioAgregador;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class ServicioAgregadorApplicationTests {
//
//	@Test
//	void contextLoads() {
//	}


import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Categoria;
import ar.utn.ba.dsi.servicioAgregador.models.entities.hechos.Ubicacion;
import ar.utn.ba.dsi.servicioAgregador.services.impl.NormalizacionService;
import org.springframework.web.reactive.function.client.WebClient;

public class ServicioAgregadorApplicationTests {
	/*public static void main(String[] args) {
		WebClient.Builder webClientBuilder = WebClient.builder();
		NormalizacionService ns = new NormalizacionService(webClientBuilder);
		Ubicacion u = new Ubicacion(-29.6849372775783, -67.1817575814487, null);
		Ubicacion normalizada = ns.normalizarUbicacion(u);
		System.out.println("Provincia: " + normalizada.getProvincia());
		System.out.println("Lat/Lon: " + normalizada.getLatitud() + " / " + normalizada.getLongitud());
	}*/

	public static void main(String[] args) {
		WebClient.Builder webClientBuilder = WebClient.builder();
		NormalizacionService ns = new NormalizacionService(webClientBuilder);
		Categoria c = new Categoria(1, "choque");
		Categoria cNormalizada = ns.normalizarCategoria(c);
		System.out.println("Categoria: " + cNormalizada.getNombre());
	}
}

