package ar.utn.ba.dsi.servicioAgregador.config;

import ar.utn.ba.dsi.servicioAgregador.models.entities.colecciones.fuentes.Fuente;
import ar.utn.ba.dsi.servicioAgregador.models.repositories.IFuenteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

	private final IFuenteRepository fuenteRepository;

	// Leemos las rutas del properties
	@Value("${dinamica.ruta}")
	private String rutaDinamica;

	@Value("${estatica.ruta}")
	private String rutaEstatica;

	// Inyectar la de proxy si la tienes en properties también
	@Value("${proxy.ruta}")
	private String rutaProxy;

	public DataInitializer(IFuenteRepository fuenteRepository) {
		this.fuenteRepository = fuenteRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("🚀 Inicializando Fuentes Base...");

		crearOActualizarFuente("DINAMICA", rutaDinamica);
		crearOActualizarFuente("ESTATICA", rutaEstatica);
		// crearOActualizarFuente("PROXY", rutaProxy);
	}

	private void crearOActualizarFuente(String nombre, String url) {
		Optional<Fuente> fuenteOpt = fuenteRepository.findByNombreFuenteIgnoreCase(nombre);

		if (fuenteOpt.isPresent()) {
			// Si ya existe, actualizamos la URL por si cambió el entorno (Dev -> Prod)
			Fuente f = fuenteOpt.get();
			if (!f.getUrl().equals(url)) {
				f.setUrl(url);
				fuenteRepository.save(f);
				System.out.println("✅ Fuente actualizada: " + nombre + " -> " + url);
			}
		} else {
			// Si no existe, la creamos
			Fuente f = new Fuente(nombre, url);
			fuenteRepository.save(f);
			System.out.println("✅ Fuente creada: " + nombre + " -> " + url);
		}
	}
}