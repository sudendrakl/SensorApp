package com.blackbeard.sensors.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.blackbeard.sensors.BuildConfig;
import okhttp3.MediaType;

/**
 * Created by sudendra.kamble on 29/09/16.
 */

public interface Constants {

  interface URLS {

    String REGISTER = BuildConfig.BASE_URL;
  }
  MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

  String PREFS = "shared_pref";
  String PREF_TOKEN = "token";

  String ACTION_START_SENSOR_ACTIVITY = "com.blackbeard.sensor.sensoractivity";
}
