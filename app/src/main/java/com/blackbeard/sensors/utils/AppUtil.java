package com.blackbeard.sensors.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by sudendra.kamble on 29/09/16.
 */

public class AppUtil {

  public static String getImeiOrUniqueID(Context context) {
    String imeiNumber = null;
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (tm != null) imeiNumber = tm.getDeviceId();
    if (imeiNumber == null || imeiNumber.length() == 0) {
      imeiNumber =
          Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    return imeiNumber;
  }
}
