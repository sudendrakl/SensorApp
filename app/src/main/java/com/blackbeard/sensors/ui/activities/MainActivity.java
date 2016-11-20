package com.blackbeard.sensors.ui.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.api.dto.APIResponseDto;
import com.blackbeard.sensors.api.dto.DeviceInfoDto;
import com.blackbeard.sensors.ui.fragments.AccelerometerFragment_;
import com.blackbeard.sensors.ui.fragments.BarometerFragment_;
import com.blackbeard.sensors.ui.fragments.BatteryFragment_;
import com.blackbeard.sensors.ui.fragments.BluetoothFragment_;
import com.blackbeard.sensors.ui.fragments.GPSFragment_;
import com.blackbeard.sensors.ui.fragments.GyroscopeFragment_;
import com.blackbeard.sensors.ui.fragments.NFCFragment_;
import com.blackbeard.sensors.ui.fragments.ProximityFragment_;
import com.blackbeard.sensors.ui.fragments.StepCounterFragment_;
import com.blackbeard.sensors.ui.fragments.ThermometerFragment_;
import com.blackbeard.sensors.ui.fragments.AccelerometerFragment;
import com.blackbeard.sensors.ui.fragments.BarometerFragment;
import com.blackbeard.sensors.ui.fragments.BatteryFragment;
import com.blackbeard.sensors.ui.fragments.BluetoothFragment;
import com.blackbeard.sensors.ui.fragments.GPSFragment;
import com.blackbeard.sensors.ui.fragments.GyroscopeFragment;
import com.blackbeard.sensors.ui.fragments.NFCFragment;
import com.blackbeard.sensors.ui.fragments.ProximityFragment;
import com.blackbeard.sensors.ui.fragments.StepCounterFragment;
import com.blackbeard.sensors.ui.fragments.ThermometerFragment;
import com.blackbeard.sensors.utils.AppUtil;
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.PreferencesUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int MY_PERMISSIONS_REQUEST = 8976;
  private Toolbar toolbar;
  private SearchView searchView;
  private Timer autoSyncTimer;
  private TimerTask autoSyncTimerTask;
  private final OkHttpClient client = new OkHttpClient();
  private boolean firstSync;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //Add all fragments here
    addFragments();
  }

  @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    autoSyncTimer = new Timer();
    autoSyncTimerTask = new TimerTask() {
      @Override public void run() {
        uploadAllData();
      }
    };
    autoSyncTimer.scheduleAtFixedRate(autoSyncTimerTask, Constants.SYNC_INITIAL_DELAY, Constants.SYNC_PERIOD);
  }

  private void refresh() {
    removeAllFragments();
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

    fragmentTransaction.commit();
  }

  private void removeAllFragments() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    List<Fragment> fragmentList = fragmentManager.getFragments();
    if (fragmentList != null) {
      for (int i = fragmentList.size() - 1; i >= 0; --i) {
        Fragment fragment = fragmentList.get(i);
        if (fragment != null) {
          fragmentTransaction.remove(fragment).commitNow();
        }
      }
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

  }

  private void uploadAllData() {

    try {
      FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();

      List jsonArray = new ArrayList();
      jsonArray.add(((ThermometerFragment) fragmentManager.findFragmentByTag(ThermometerFragment.TAG)).getData());
      jsonArray.add(((StepCounterFragment) fragmentManager.findFragmentByTag(StepCounterFragment.TAG)).getData());
      jsonArray.add(((ProximityFragment) fragmentManager.findFragmentByTag(ProximityFragment.TAG)).getData());
      jsonArray.add(((NFCFragment) fragmentManager.findFragmentByTag(NFCFragment.TAG)).getData());
      jsonArray.add(((GyroscopeFragment) fragmentManager.findFragmentByTag(GyroscopeFragment.TAG)).getData());
      jsonArray.add(((GPSFragment) fragmentManager.findFragmentByTag(GPSFragment.TAG)).getData());
      jsonArray.add(((BluetoothFragment) fragmentManager.findFragmentByTag(BluetoothFragment.TAG)).getData());
      jsonArray.add(((BatteryFragment) fragmentManager.findFragmentByTag(BatteryFragment.TAG)).getData());
      jsonArray.add(((BarometerFragment) fragmentManager.findFragmentByTag(BarometerFragment.TAG)).getData());
      jsonArray.add(((AccelerometerFragment) fragmentManager.findFragmentByTag(AccelerometerFragment.TAG)).getData());

      final DeviceInfoDto deviceInfoDto = new DeviceInfoDto(jsonArray);
      deviceInfoDto.setUid(AppUtil.getImeiOrUniqueID(getApplicationContext()));
      deviceInfoDto.setTimestampMillis(System.currentTimeMillis());
      sendRequest(Constants.URLS.ADD_ENTRY, Constants.GSON.toJson(deviceInfoDto));

    } catch (IOException | JSONException e) {
      //Failed to update? just ignore
      e.printStackTrace();
    }
  }

  private void sendRequest(String url, String postParams) throws IOException {
    RequestBody body = RequestBody.create(Constants.JSON_TYPE_MARKDOWN, postParams);
    Request request = new Request.Builder().url(url)
        .header("Authorization", PreferencesUtil.getToken(this))
        .post(body)
        .build();

    Response response = client.newCall(request).execute();
    try {
      String responseString = new String(response.body().bytes());
      if (!response.isSuccessful()) {
        handleFailure(response, responseString);
      } else {
        handleSuccess(response, responseString);
      }
    } catch (IOException ex) {
      Log.e(TAG, "Some shit happened", ex);
    }
  }

  void handleFailure(Response response, String responseString) throws IOException {
    int responseCode = response.code();
    Log.d(TAG, String.format("handleFailure(%d): %s", responseCode, responseString));
    APIResponseDto responseParse = Constants.GSON.fromJson(responseString, APIResponseDto.class);
    if (responseCode == 400 || responseCode == 500) {
      //Snackbar.make(toolbar, responseParse.getMessage(), Snackbar.LENGTH_INDEFINITE)
      //    .setAction("OK", null)
      //    .show();
    } else if (responseCode == 401 || responseCode == 403) { //unauthorised //clear token, redirect to login from here
      PreferencesUtil.saveToken(this, null);
      finish();
      startActivity(new Intent(this, LoginActivity.class));
      //Snackbar.make(toolbar, "Unauthorised", Snackbar.LENGTH_INDEFINITE)
      //    .setAction("OK", null)
      //    .show();
    } else {
      //Snackbar.make(toolbar, "Unknown server error", Snackbar.LENGTH_INDEFINITE)
      //    .setAction("OK", null)
      //    .show();
    }
    if(!firstSync) {
      Snackbar.make(toolbar, "Failed to sync", Snackbar.LENGTH_SHORT)
          //.setAction("OK", null)
          .show();
    }
  }

  void handleSuccess(Response response, String responseString) throws IOException {
    Log.d(TAG, String.format("handleSuccess(%d): %s", response.code(), responseString));
    APIResponseDto responseParse = Constants.GSON.fromJson(responseString, APIResponseDto.class);
    if (responseParse.isStatus()) {
      if(!firstSync) {
        Snackbar.make(toolbar, "Sensor data synced", Snackbar.LENGTH_SHORT)
            //.setAction("OK", null)
            .show();
      }
    } else {
      handleFailure(response, responseString);
    }
    firstSync = true;
  }

  BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if ("check_permission".equals(intent.getAction())) {
        checkPermissionShit();
      }
    }
  };

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main_activity, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_search) {
      startActivity(new Intent(this, SearchActivity.class));
    } else if (item.getItemId() == R.id.action_logout) {
      logout();
    } else if (item.getItemId() == R.id.action_sync) {
      firstSync = false;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override protected void onStart() {
    super.onStart();
    IntentFilter intentFilter = new IntentFilter("check_permission");
    registerReceiver(receiver, intentFilter);
    checkPermissionShit();
  }

  private void checkPermissionShit() {
    // Here, thisActivity is the current activity
    String permissionArray[] = new String[] {
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH, Manifest.permission.NFC,
    };
    for (String permission : permissionArray) {
      if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
          Snackbar.make(toolbar, "Grant permission in settings", Snackbar.LENGTH_INDEFINITE)
              .setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View v) {
                  Log.i(TAG, "Grant permission in settings");
                }
              }).show();
        } else {
          ActivityCompat.requestPermissions(this, permissionArray, MY_PERMISSIONS_REQUEST);
          break;
        }
      }
    }
  }

  private void logout() {
    Request request = new Request.Builder().url(Constants.URLS.LOGOUT)
        .header("Authorization", PreferencesUtil.getToken(this))
        .build();

    client.newCall(request).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        Log.d(TAG, "failed to log off....");
        e.printStackTrace();
        Snackbar.make(toolbar, "Failed to logout, please try again", Snackbar.LENGTH_INDEFINITE).show();
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        Log.d(TAG, "logged out....");
        PreferencesUtil.saveToken(MainActivity.this.getApplicationContext(), null);
        MainActivity.this.finish();
        MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
      }
    });
  }

  @Override protected void onStop() {
    unregisterReceiver(receiver);
    autoSyncTimer.cancel();
    super.onStop();
  }
}
