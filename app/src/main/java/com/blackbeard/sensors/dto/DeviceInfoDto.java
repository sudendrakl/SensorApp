package com.blackbeard.sensors.dto;

import android.content.Context;
import com.bizapps.sensors.BuildConfig;
import com.google.gson.JsonArray;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.json.JSONArray;

/**
 * Created by sudendra.kamble on 05/10/16.
 */
@Data
@ToString
public class DeviceInfoDto {
  String device;//:name,
  String model;//:Moto X Force,
  String manufacturer;//: Motorola,
  String systemVersionName;//: Android M,
  String systemVersion;//: 21,
  String appVersion;//: 1.1.3,
  List<String> hardwareDetails;//: [ {gps:{lat:16.7457, lon:77.5643}} ]

  public DeviceInfoDto(List<String> hardwareDetail) {
    try {
      device = android.os.Build.BOARD;
      model = android.os.Build.BRAND;
      manufacturer = android.os.Build.DEVICE;
      device = android.os.Build.MODEL;
      model = android.os.Build.PRODUCT;

      systemVersion = android.os.Build.VERSION.RELEASE;
      systemVersionName = android.os.Build.VERSION.INCREMENTAL;
      appVersion = BuildConfig.VERSION_NAME;
      hardwareDetails = hardwareDetail;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
