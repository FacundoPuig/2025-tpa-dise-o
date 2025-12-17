package ar.utn.ba.dsi.servicioUsuarios;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

  // Almacena un Bucket por cada IP para contar intentos
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  // Almacena la "hora de liberación" de las IPs baneadas
  private final Map<String, Long> bans = new ConcurrentHashMap<>();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String clientIp = getClientIP(httpRequest);

    // 1. VERIFICAR SI ESTÁ BANEADO ACTUALMENTE
    if (isBanned(clientIp)) {
      long secondsLeft = getSecondsLeft(clientIp);
      sendErrorResponse(response, "⛔ Estás bloqueado temporalmente.", secondsLeft);
      return;
    }

    // 2. OBTENER O CREAR EL BUCKET PARA ESTA IP
    Bucket bucket = buckets.computeIfAbsent(clientIp, this::createNewBucket);

    // 3. INTENTAR CONSUMIR 1 INTENTO
    if (bucket.tryConsume(1)) {
      // ¡ÉXITO! Pasamos la petición

      // Agregamos el header informativo para el Frontend
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.addHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));

      chain.doFilter(request, response);

    } else {
      // ¡FALLO! Se acabaron los tokens -> APLICAR BANEO
      applyBan(clientIp);

      // Informamos que el bloqueo empieza ahora (60 segundos)
      sendErrorResponse(response, "⛔ Límite de intentos excedido.", 60);
    }
  }

  /**
   * Configuración Estricta: 5 intentos por minuto (Login/Registro).
   * Usa 'intervally' para que la recarga no sea gradual, sino por bloque.
   */
  private Bucket createNewBucket(String key) {
    Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
    return Bucket.builder().addLimit(limit).build();
  }

  /**
   * Envía la respuesta de error 429 con el JSON que espera el Frontend.
   */
  private void sendErrorResponse(ServletResponse response, String message, long secondsLeft) throws IOException {
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.setStatus(429); // Too Many Requests
    httpResponse.setContentType("application/json");
    httpResponse.setCharacterEncoding("UTF-8");

    // JSON: {"error": "...", "retryAfter": 45}
    String jsonBody = String.format("{\"error\": \"%s\", \"retryAfter\": %d}", message, secondsLeft);
    httpResponse.getWriter().write(jsonBody);
  }

  // --- MÉTODOS AUXILIARES DE BANEO ---

  private void applyBan(String ip) {
    // Baneo por 60 segundos desde este momento
    bans.put(ip, System.currentTimeMillis() + 60000);
  }

  private boolean isBanned(String ip) {
    Long expiration = bans.get(ip);
    if (expiration == null) return false;

    if (System.currentTimeMillis() > expiration) {
      bans.remove(ip); // El tiempo ya pasó, liberamos la IP
      buckets.remove(ip); // Reseteamos su bucket para que empiece limpio
      return false;
    }
    return true; // Sigue baneado
  }

  private long getSecondsLeft(String ip) {
    Long expiration = bans.get(ip);
    if (expiration == null) return 0;
    return Math.max(0, (expiration - System.currentTimeMillis()) / 1000);
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0];
  }
}