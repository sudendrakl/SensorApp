package com.blackbeard.sensors.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by sudendra.kamble on 29/09/16.
 */
@AllArgsConstructor(suppressConstructorProperties = true)
public class TokenDto extends APIResponseDto{
  String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
