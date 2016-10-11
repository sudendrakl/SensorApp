package com.blackbeard.sensors.dto;

import lombok.Data;

/**
 * Created by sudendra.kamble on 10/10/16.
 */
@Data
public class GyroDto {
  boolean isAvailable, isEnabled;
  float v1,v2,v3;
}
