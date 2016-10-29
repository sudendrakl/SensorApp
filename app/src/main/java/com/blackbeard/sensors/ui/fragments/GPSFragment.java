package com.blackbeard.sensors.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.BackgroundLocationService;
import com.blackbeard.sensors.api.dto.GPSDto;
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.LocationUtils;
import com.google.android.gms.location.LocationResult;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

@EFragment(R.layout.fragment_plus_one) public class GPSFragment extends Fragment {
  public static final String TAG = "gps";

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;
  Context context;

  boolean isEnabled;
  boolean isAvailable;
  List<String> provider;
  String accuracyMode;
  Location location;
  Timer timer;
  TimerTask timerTask;

  @AfterInject  void init() {
    context = getActivity();

    Intent intent = new Intent(context, BackgroundLocationService.class);
    context.startService(intent);
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override public void run() {
        if (location != null) updateOptionText();
      }
    };
  }

  @AfterViews  void initViews() {
    title.setText("GPS");
    updateStatus();
      timer.scheduleAtFixedRate(timerTask, 0, Constants.UPDATE_UI_DELAY);
  }

  private void updateStatus() {
    isAvailable = LocationUtils.hasGPS(context);
    provider = LocationUtils.getProvider(context);
    isEnabled = LocationUtils.isGPSEnabled(context);
    accuracyMode = LocationUtils.getLocationModeStr(context);
    updateText(
        String.format("Available:%s Provider:%s Enabled:%s Accuracy:%s", isAvailable, provider,
            isEnabled, accuracyMode));
  }

  @Receiver(actions = Constants.ACTION_BROADCAST_LOCATION, registerAt = Receiver.RegisterAt.OnResumeOnPause)
   void onLocationChanged(Intent intent) {
    if (LocationResult.hasResult(intent)) {
      LocationResult locationResult = LocationResult.extractResult(intent);
      location = locationResult.getLastLocation();
    } else {
      location = intent.getParcelableExtra("location");
    }

    if (location != null) {
      Log.d(TAG,
          "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " lon: "
              + location.getLongitude());
      //INFO: ui updated with timer task
      //updateOptionText(location);
    }
    updateStatus();
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(String text) {
    content.setText(text);
  }


  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateOptionText() {
    optionContent.setVisibility(View.VISIBLE);
    optionContent.setText(
        String.format(Locale.ENGLISH, "Latitude:%.3f  Longitude:%.3f Accuracy:%.3s", location.getLatitude(),
            location.getLongitude(), location.getAccuracy()));
  }
  
  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  @UiThread  void handlePostDetach() {
    Intent intent = new Intent(context, BackgroundLocationService.class);
    context.stopService(intent);
    timer.cancel();
  }


  public HashMap<String, GPSDto> getData() throws JSONException {
    GPSDto aDto = new GPSDto();
    aDto.setEnabled(isEnabled);
    aDto.setAccuracyMode(LocationUtils.getLocationMode(context));
    aDto.setAvailable(isAvailable);
    aDto.setProvider(provider);
    if (location != null) {
      aDto.setLatitude(location.getLatitude());
      aDto.setLongitude(location.getLongitude());
      aDto.setAccuracy(location.getAccuracy());
    }
    HashMap<String, GPSDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG, aDto);
    return hashMap;// Constants.GSON.toJson(hashMap);
  }
}