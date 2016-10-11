package com.blackbeard.sensors.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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


  public static boolean isGooglePlayServicesAvailable(Context context) {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(context);

    if (result != ConnectionResult.SUCCESS) {
      if (googleAPI.isUserResolvableError(result)) {
        return false;
      }
      //TODO: Dont allow app usage
      return false;
    }

    return true;
  }

  private static boolean checkGooglePlayServices(Activity activity, int requestCode) {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(activity);
    if (result != ConnectionResult.SUCCESS) {
      if (googleAPI.isUserResolvableError(result)) {
        googleAPI.getErrorDialog(activity, result, requestCode).show();
      }
      //TODO: Dont allow app usage
      return false;
    }

    return true;

  //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  //  switch (requestCode) {
  //    case REQUEST_GOOGLE_PLAY_SERVICES:
  //      if (resultCode != Activity.RESULT_OK) {
  //        finish(); // dont allow to proceed if didnt update
  //      }
  //      break;
  //  }
  //}
  }



  public static void hideKeyBoard(View view) {
    final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static void showKeyBoard(View view) {
    final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
  }
}
