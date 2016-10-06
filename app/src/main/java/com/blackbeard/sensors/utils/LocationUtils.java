package com.blackbeard.sensors.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import java.util.List;

/**
 * Created by sudendra.kamble on 05/10/16.
 */
public class LocationUtils {

  static public boolean isGPSEnabled(Context context) {
    Log.d("CommonUtils", "isGPSEnabled");
    final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

    boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    int mode = getLocationMode(context);
    switch (mode) {
      case Settings.Secure.LOCATION_MODE_OFF:
      case Settings.Secure.LOCATION_MODE_BATTERY_SAVING: // battery saving with wifi triangulation
      case Settings.Secure.LOCATION_MODE_SENSORS_ONLY: //GPS still on, but only GPS and no n/w or wifi triangulation
        gpsEnabled = false;
        break;
      case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
        gpsEnabled &= true;
        break;
      case -1:
        //do nothing, since device api < api 19

    }
    return gpsEnabled;
  }

  static public String getLocationModeStr(Context context) {
    switch (getLocationMode(context)) {
      case Settings.Secure.LOCATION_MODE_OFF:
        return "off";
      case Settings.Secure.LOCATION_MODE_BATTERY_SAVING: // battery saving with wifi triangulation
        return "battery saving";
      case Settings.Secure.LOCATION_MODE_SENSORS_ONLY: //GPS still on, but only GPS and no n/w or wifi triangulation
        return "gps only";
      case Settings.Secure.LOCATION_MODE_HIGH_ACCURACY:
        return "high accuracy";
      default:
        return "unknown";
    }
  }

  private static int getLocationMode(Context context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
      }
    } catch (Settings.SettingNotFoundException e) {
      e.printStackTrace();
    }
    return -1;
  }

  static public String getProvider(Context context) {
    final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    List<String> list = manager.getProviders(true);

    return list != null ? list.toString() : ""; //enabled providers only
  }

  static public boolean hasGPS(Context context) {
    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
  }
}
