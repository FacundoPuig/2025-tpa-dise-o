package ar.utn.ba.dsi.fuenteDinamica.services.impl;

import ar.utn.ba.dsi.fuenteDinamica.services.IFileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService implements IFileStorageService {

  private final Path uploadLocation;

  public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
    this.uploadLocation = Paths.get(uploadDir);
    try {
      Files.createDirectories(this.uploadLocation);
    } catch (IOException e) {
      throw new RuntimeException("No se pudo crear el directorio de almacenamiento.", e);
    }
  }

  @Override
  public String guardar(MultipartFile archivo) {
    try {
      if (archivo.isEmpty()) {
        throw new IllegalStateException("No se puede guardar un archivo vacío.");
      }
      String originalFilename = StringUtils.cleanPath(archivo.getOriginalFilename());
      String extension = StringUtils.getFilenameExtension(originalFilename);
      String nombreArchivoUnico = UUID.randomUUID().toString() + "." + extension;

      Path destinationFile = this.uploadLocation.resolve(nombreArchivoUnico).normalize().toAbsolutePath();
      try (InputStream inputStream = archivo.getInputStream()) {
        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
      }
      return nombreArchivoUnico;
    } catch (IOException e) {
      throw new RuntimeException("Falló al guardar el archivo.", e);
    }
  }

  @Override
  public Resource cargarComoRecurso(String nombreArchivo) {
    try {
      Path filePath = this.uploadLocation.resolve(nombreArchivo).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists() && resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("No se pudo leer el archivo: " + nombreArchivo);
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error en la ruta del archivo: " + nombreArchivo, e);
    }
  }

  @Override
  public void eliminar(String nombreArchivo) {
    try {
      Path filePath = this.uploadLocation.resolve(nombreArchivo).normalize();
      Files.deleteIfExists(filePath);
    } catch (IOException e) {
      throw new RuntimeException("No se pudo eliminar el archivo: " + nombreArchivo, e);
    }
  }


}
