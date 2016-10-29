package com.blackbeard.sensors.api.dto;

import java.util.List;
import lombok.Data;

/**
 * Created by sudendra.kamble on 10/10/16.
 */
@Data
public class GPSDto {
  boolean isEnabled;
  boolean isAvailable;
  List<String> provider;
  int accuracyMode;
  double latitude;
  double longitude;
  double accuracy;
}
