package com.blackbeard.sensors.api.dto;

import lombok.Data;

/**
 * Created by sudendra.kamble on 10/10/16.
 */
@Data
public class BarometerDto {
  boolean isAvailable, isEnabled;
  float pressure;
}
