package com.blackbeard.sensors.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by sudendra.kamble on 05/10/16.
 */
@Data
public class APIResponseDto {
  String status;
  String message;
  int code;
}
