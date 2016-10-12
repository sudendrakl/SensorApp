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
import com.blackbeard.sensors.dto.APIResponseDto;
import com.blackbeard.sensors.dto.DeviceInfoDto;
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
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.PreferencesUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final int MY_PERMISSIONS_REQUEST = 8976;
  private FloatingActionButton refreshButton;
  private Toolbar toolbar;
  private SearchView searchView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    refreshButton = (FloatingActionButton) findViewById(R.id.fab);
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
        uploadAllData();
        Snackbar.make(view, "Sync feature", Snackbar.LENGTH_LONG)
            //.setAction("Action", null)
            .show();
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


  private final OkHttpClient client = new OkHttpClient();

  private void uploadAllData() {

    new Thread(new Runnable() {
      @Override public void run() {
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

          sendRequest(Constants.URLS.ADD_ENTRY, Constants.GSON.toJson(deviceInfoDto));

          //TODO: do something with this shit addAppResponse
        } catch (IOException | JSONException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void sendRequest(String url, String postParams)
      throws IOException {
    RequestBody body = RequestBody.create(Constants.JSON_TYPE_MARKDOWN, postParams);
    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", PreferencesUtil.getToken(this))
        .post(body)
        .build();

    Response response = client.newCall(request).execute();

    try {
      if (!response.isSuccessful()) {
        handleFailure(response);
      } else {
        handleSuccess(response);
      }
    } catch (IOException ex) {
      Log.e(TAG, "Some shit happened", ex);
    }
  }


  void handleFailure(Response response) throws IOException {
    APIResponseDto responseParse =
        Constants.GSON.fromJson(new String(response.body().bytes()), APIResponseDto.class);
    if (response.code() == 400 || response.code() == 500) {
      Snackbar.make(refreshButton, responseParse.getMessage(), Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", null)
          .show();
    } else if (response.code() == 401) {
      Snackbar.make(refreshButton, "Unauthorised", Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", null)
          .show();
    } else {
      Snackbar.make(refreshButton, "Unknown server error", Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", null)
          .show();
    }
  }

  void handleSuccess(Response response) throws IOException {
    APIResponseDto responseParse = Constants.GSON.fromJson(new String(response.body().bytes()), APIResponseDto.class);
    //TODO: updated response
    Log.i(TAG, response.toString());

    if(responseParse.isStatus()) {
      //TODO: what to do
      Snackbar.make(refreshButton, responseParse.getMessage(), Snackbar.LENGTH_LONG).setAction("OK", null).show();
    } else {
      handleFailure(response);
    }
  }


  BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if("check_permission".equals(intent.getAction())) {
          checkPermissionShit();
      }
    }
  };

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main_activity, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == R.id.action_search) {
      startActivity(new Intent(this, SearchActivity.class));
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
        Manifest.permission.BLUETOOTH, Manifest.permission.BATTERY_STATS, Manifest.permission.NFC,
    };
    for(String permission:permissionArray) {
      if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
          Snackbar.make(refreshButton, "Grant permission in settings", Snackbar.LENGTH_INDEFINITE)
              .setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View v) {
                  Log.i(TAG, "Grant permission in settings");
                }
              });
        } else {
          ActivityCompat.requestPermissions(this, new String[] { permission },
              MY_PERMISSIONS_REQUEST);
        }
      }
    }
  }

  @Override protected void onStop() {
    unregisterReceiver(receiver);
    super.onStop();
  }
}
