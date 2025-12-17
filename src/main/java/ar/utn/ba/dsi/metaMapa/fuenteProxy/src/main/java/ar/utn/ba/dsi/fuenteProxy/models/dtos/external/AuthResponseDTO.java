package ar.utn.ba.dsi.fuenteProxy.models.dtos.external;


import lombok.Data;

@Data
public class AuthResponseDTO {
  private boolean error;
  private String message;
  private LoginDataDTO data;
}
