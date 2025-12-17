package ar.utn.ba.dsi.servicioAgregador.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesPermissionsDTO {
  private String email;
  private String nombreRol;
  private List<String> permisos;
}