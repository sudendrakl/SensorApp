package com.blackbeard.sensors.dto;

import android.location.Location;
import lombok.Data;

/**
 * Created by sudendra.kamble on 10/10/16.
 */
@Data
public class GPSDto {
  boolean isEnabled;
  boolean isAvailable;
  String provider;
  String accuracyMode;
  Location location;
}
