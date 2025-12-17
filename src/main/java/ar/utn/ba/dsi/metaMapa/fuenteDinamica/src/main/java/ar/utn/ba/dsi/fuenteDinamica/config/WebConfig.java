package ar.utn.ba.dsi.fuenteDinamica.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Mapea las peticiones que empiecen con "/uploads/**"
    // a la carpeta física "uploads" en la raíz de tu proyecto.
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:uploads/");
  }
}