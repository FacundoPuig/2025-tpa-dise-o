package ar.utn.ba.dsi.fuenteProxy.models.dtos.external;

import lombok.Data;

@Data
public class LoginDataDTO {
  private String access_token;
  private String token_type;
  private UserDataDTO user;
}
