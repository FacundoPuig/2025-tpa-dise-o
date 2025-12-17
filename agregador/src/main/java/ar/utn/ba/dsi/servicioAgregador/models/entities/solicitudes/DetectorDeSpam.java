package ar.utn.ba.dsi.servicioAgregador.models.entities.solicitudes;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class DetectorDeSpam implements IDetectorDeSpam {

  // Si el puntaje supera 50, se marca como SPAM
  private static final int UMBRAL_SPAM = 50;

  // Lista de palabras comunes en spam (Apuestas, estafas, contenido adulto)
  private static final List<String> PALABRAS_PROHIBIDAS = Arrays.asList(
      "casino", "gratis", "ganar dinero", "bitcoin", "crypto", "viagra",
      "oferta", "click aqui", "sorteo", "hot", "xxx", "premio", "regalo" ,"trabajo desde casa", "dinero facil", "inversion", "urgente"
  );

  // Expresiones regulares para detectar patrones sospechosos
  private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");
  private static final Pattern REPETICION_LETRAS = Pattern.compile("(.)\\1{4,}"); // Ej: "Hoooooola"

  @Override
  public boolean esSpam(String motivo) {
    if (motivo == null || motivo.trim().isEmpty()) return true; // Motivo vacío es sospechoso

    int puntaje = 0;
    String textoAnalisis = motivo.toLowerCase();

    // 1. Penalización por Palabras Prohibidas (+20 pts c/u)
    for (String palabra : PALABRAS_PROHIBIDAS) {
      if (textoAnalisis.contains(palabra)) {
        puntaje += 20;
        System.out.println("SPAM DETECTADO: Palabra prohibida -> " + palabra);
      }
    }

    // 2. Penalización por Enlaces (+30 pts)
    // Un motivo de eliminación rara vez necesita un link, y menos si es spam.
    if (URL_PATTERN.matcher(motivo).find()) {
      puntaje += 30;
      System.out.println("SPAM DETECTADO: Contiene URL.");
    }

    // 3. Penalización por "Gritos" (Mayúsculas) (+15 pts)
    if (esGrito(motivo)) {
      puntaje += 15;
      System.out.println("SPAM DETECTADO: Exceso de mayúsculas.");
    }

    // 4. Penalización por Repetición de caracteres (+15 pts)
    // Ej: "Eliminen esto aaaaaaaaaaaaaaa"
    if (REPETICION_LETRAS.matcher(motivo).find()) {
      puntaje += 15;
      System.out.println("SPAM DETECTADO: Repetición de caracteres.");
    }

    // 5. Penalización por Longitud Irrelevante (+10 pts)
    // Motivos muy cortos como "malo" o "falso" sin explicación
    if (motivo.length() < 10) {
      puntaje += 10;
    }

    System.out.println("Puntaje final de SPAM para el motivo: " + puntaje + " (Umbral: " + UMBRAL_SPAM + ")");

    return puntaje >= UMBRAL_SPAM;
  }

  // Detecta si más del 60% del texto está en mayúsculas (ignorando espacios y símbolos)
  private boolean esGrito(String texto) {
    long mayusculas = texto.chars().filter(Character::isUpperCase).count();
    long letras = texto.chars().filter(Character::isLetter).count();

    if (letras == 0) return false; // Si no hay letras (solo números/símbolos), no cuenta

    return (double) mayusculas / letras > 0.60;
  }
}