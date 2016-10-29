package com.blackbeard.sensors.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sudendra.kamble on 05/10/16.
 */

public class PreferencesUtil {

  static public void saveToken(Context context, String token) {
    SharedPreferences pref = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
    pref.edit().putString(Constants.PREF_TOKEN, token).apply();
  }

  static public String getToken(Context context) {
    SharedPreferences pref = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
    return pref.getString(Constants.PREF_TOKEN, null);
  }
}
