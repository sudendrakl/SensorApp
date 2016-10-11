package com.blackbeard.sensors.dto;

import lombok.AllArgsConstructor;
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
