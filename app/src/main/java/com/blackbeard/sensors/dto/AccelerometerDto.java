package com.blackbeard.sensors.dto;

import lombok.Data;

/**
 * Created by sudendra.kamble on 10/10/16.
 */
@Data
public class AccelerometerDto {
  boolean isAvailable, isEnabled;
  float x, y, z;
}
