package com.blackbeard.sensors.dto;

import java.util.ArrayList;
import lombok.Data;

/**
 * Created by sudendra.kamble on 10/10/16.
 */

@Data
public class BluetoothDto {
  ArrayList<String> devicesList = new ArrayList<>(5);
  String status = "off";
  String bluetoothOnStatus;
}
