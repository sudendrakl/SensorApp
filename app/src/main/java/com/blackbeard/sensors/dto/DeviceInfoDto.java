package com.blackbeard.sensors.dto;

import android.os.Build;
import com.bizapps.sensors.BuildConfig;
import java.lang.reflect.Field;
import java.util.List;
import lombok.Data;
import lombok.ToString;

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
  List hardwareDetails;//: [ {gps:{lat:16.7457, lon:77.5643}} ]
  String userName;
  String mailId;

  public DeviceInfoDto(List hardwareDetail) {
    try {
      device = android.os.Build.BOARD;
      model = android.os.Build.BRAND;
      manufacturer = android.os.Build.DEVICE;
      device = android.os.Build.MODEL;
      model = android.os.Build.PRODUCT;

      systemVersion = android.os.Build.VERSION.RELEASE;
      Field[] fields = Build.VERSION_CODES.class.getFields();
      systemVersionName = fields[Build.VERSION.SDK_INT + 1].getName();
      appVersion = BuildConfig.VERSION_NAME;
      hardwareDetails = hardwareDetail;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
