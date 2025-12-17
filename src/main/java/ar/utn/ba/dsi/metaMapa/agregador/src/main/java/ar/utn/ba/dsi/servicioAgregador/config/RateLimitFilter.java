package ar.utn.ba.dsi.servicioAgregador.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter implements Filter {

  private final Bucket bucket;

  public RateLimitFilter() {
    Bandwidth limit = Bandwidth.classic(250, Refill.greedy(250, Duration.ofMinutes(1)));
    this.bucket = Bucket.builder().addLimit(limit).build();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (bucket.tryConsume(1)) {
      chain.doFilter(request, response);
    } else {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(429); // Too Many Requests
      httpResponse.getWriter().write("Has excedido el limite de peticiones. Intenta mas tarde.");
    }
  }
}