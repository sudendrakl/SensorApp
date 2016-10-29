package com.blackbeard.sensors.api.dto;

import lombok.Data;

/**
 * Created by sudendra.kamble on 05/10/16.
 */
@Data
public class APIResponseDto {
  boolean status;
  String message;
  int code;
}
