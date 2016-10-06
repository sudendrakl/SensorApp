package com.blackbeard.sensors;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.APIResponseDto;
import com.blackbeard.sensors.dto.DeviceInfoDto;
import com.blackbeard.sensors.dto.RegisterDto;
import com.blackbeard.sensors.dto.TokenDto;
import com.blackbeard.sensors.fragments.AccelerometerFragment;
import com.blackbeard.sensors.fragments.AccelerometerFragment_;
import com.blackbeard.sensors.fragments.BarometerFragment;
import com.blackbeard.sensors.fragments.BarometerFragment_;
import com.blackbeard.sensors.fragments.BatteryFragment;
import com.blackbeard.sensors.fragments.BatteryFragment_;
import com.blackbeard.sensors.fragments.BluetoothFragment;
import com.blackbeard.sensors.fragments.BluetoothFragment_;
import com.blackbeard.sensors.fragments.GPSFragment;
import com.blackbeard.sensors.fragments.GPSFragment_;
import com.blackbeard.sensors.fragments.GyroscopeFragment;
import com.blackbeard.sensors.fragments.GyroscopeFragment_;
import com.blackbeard.sensors.fragments.NFCFragment;
import com.blackbeard.sensors.fragments.NFCFragment_;
import com.blackbeard.sensors.fragments.ProximityFragment;
import com.blackbeard.sensors.fragments.ProximityFragment_;
import com.blackbeard.sensors.fragments.StepCounterFragment;
import com.blackbeard.sensors.fragments.StepCounterFragment_;
import com.blackbeard.sensors.fragments.ThermometerFragment;
import com.blackbeard.sensors.fragments.ThermometerFragment_;
import com.blackbeard.sensors.utils.AppUtil;
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.PreferencesUtil;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int MY_PERMISSIONS_REQUEST = 8976;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton refreshButton = (FloatingActionButton) findViewById(R.id.fab);
    refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //    .setAction("Action", null)
        //    .show();
        removeAllFragments();

        addFragments();
      }
    });

    FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.fab_add);
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        //TODO: uploadAllData();
      }
    });
    //Add all fragments here
   addFragments();
  }

  private void addFragments() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.add(R.id.content_main, AccelerometerFragment_.builder().build(), AccelerometerFragment.TAG);
    fragmentTransaction.add(R.id.content_main, BarometerFragment_.builder().build(), BarometerFragment.TAG);
    fragmentTransaction.add(R.id.content_main, BatteryFragment_.builder().build(), BatteryFragment.TAG);
    fragmentTransaction.add(R.id.content_main, BluetoothFragment_.builder().build(), BluetoothFragment.TAG);
    fragmentTransaction.add(R.id.content_main, GPSFragment_.builder().build(), GPSFragment.TAG);
    fragmentTransaction.add(R.id.content_main, GyroscopeFragment_.builder().build(), GyroscopeFragment.TAG);
    fragmentTransaction.add(R.id.content_main, NFCFragment_.builder().build(), NFCFragment.TAG);
    fragmentTransaction.add(R.id.content_main, ProximityFragment_.builder().build(), ProximityFragment.TAG);
    fragmentTransaction.add(R.id.content_main, StepCounterFragment_.builder().build(), StepCounterFragment.TAG);
    fragmentTransaction.add(R.id.content_main, ThermometerFragment_.builder().build(), ThermometerFragment.TAG);

    fragmentTransaction.commitAllowingStateLoss();
  }

  private void removeAllFragments() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    fragmentTransaction.remove(fragmentManager.findFragmentByTag(AccelerometerFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(BarometerFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(BatteryFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(BluetoothFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(GPSFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(GyroscopeFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(NFCFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(ProximityFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(StepCounterFragment.TAG));
    fragmentTransaction.remove(fragmentManager.findFragmentByTag(ThermometerFragment.TAG));

    fragmentTransaction.commitAllowingStateLoss();
  }


  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //switch (requestCode) {
    //  case CommonUtils.REQUEST_CHECK_SETTINGS:
    //    //INFO: Broadcast result to RN, handle however needed
    //    Intent permissionResult = new Intent(HeroLocationModule.ACTION_GPS_PERMISSION_REQUEST);
    //    permissionResult.putExtra("gps_check", resultCode == RESULT_OK ? "RESULT_OK" : "RESULT_CANCELLED");
    //    sendBroadcast(permissionResult);
    //    break;
    //  case PLAY_SERVICES_RESOLUTION_REQUEST:
    //    //TODO
    //    if(resultCode!=RESULT_OK) {
    //      //finish(); //dont allow app usage
    //    }
    //    break;
    //  default:
    //    super.onActivityResult(requestCode, resultCode, data);
    //    break;
    //}
  }


  private final OkHttpClient client = new OkHttpClient();
  private final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .create();

  private void uploadAllData() {
    try {


      String sayData = "[{\"gps\":{\"lat\":1234,\"lon\":-23.5,\"accuracy\":30}}]";//TODO:fetch all data

      String jsonParams = gson.toJson(new DeviceInfoDto(sayData));

      HashMap<String, String> headerMap = new HashMap<>();
      headerMap.put("Content-Type", "application/json");
      headerMap.put("Authorisation", PreferencesUtil.getToken(this));
      Headers headers = Headers.of(headerMap);

      APIResponseDto addAppResponse = sendRequest(Constants.URLS.ADD_ENTRY, headers, jsonParams, APIResponseDto.class);

      //TODO: do something with this shit addAppResponse
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private <T> T sendRequest(String url, Headers headers, String postParams, Class<T> clazz)
      throws IOException {
    Request request;
    if (headers != null) {
      request = new Request.Builder().url(url)
          .headers(headers)
          .post(RequestBody.create(Constants.MEDIA_TYPE_MARKDOWN, postParams))
          .build();
    } else {
      request = new Request.Builder().url(url)
          .post(RequestBody.create(Constants.MEDIA_TYPE_MARKDOWN, postParams))
          .build();
    }

    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

    Log.i(TAG, response.body().string());

    T responseParse = gson.fromJson(response.body().charStream(), clazz);
    //TODO: updated response
    Log.i(TAG, response.toString());

    return responseParse;
  }

  BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if("check_permission".equals(intent.getAction())) {
          checkPermissionShit();
      }
    }
  };

  @Override protected void onStart() {
    super.onStart();
    IntentFilter intentFilter = new IntentFilter("check_permission");
    registerReceiver(receiver, intentFilter);
  }

  private void checkPermissionShit() {
    // Here, thisActivity is the current activity

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.BATTERY_STATS) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) == PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.READ_PHONE_STATE)) {

        // Show an expanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

      } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(this, new String[] {
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH, Manifest.permission.BATTERY_STATS, Manifest.permission.NFC,
        }, MY_PERMISSIONS_REQUEST);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    }
  }
}
