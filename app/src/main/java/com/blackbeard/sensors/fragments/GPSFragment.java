package com.blackbeard.sensors.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.BackgroundLocationService;
import com.blackbeard.sensors.dto.GPSDto;
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.LocationUtils;
import com.google.android.gms.location.LocationResult;
import java.util.HashMap;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

@EFragment(R.layout.fragment_plus_one) public class GPSFragment extends Fragment {
  public static final String TAG = GPSFragment.class.getSimpleName();

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;
  Context context;

  boolean isEnabled;
  boolean isAvailable;
  String provider;
  String accuracyMode;
  Location location;

  @AfterInject  void init() {
    context = getActivity();

    Intent intent = new Intent(context, BackgroundLocationService.class);
    context.startService(intent);
  }

  @AfterViews  void initViews() {
    title.setText("GPS");
    isAvailable = LocationUtils.hasGPS(context);
    provider = LocationUtils.getProvider(context);
    isEnabled = LocationUtils.isGPSEnabled(context);
    accuracyMode = LocationUtils.getLocationModeStr(context);
    updateText("Available:" + isAvailable + " Provider:" + provider + " Enabled:" + isEnabled + " Accuracy:" + accuracyMode);
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
      updateOptionText(location);
    }
    initViews();
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(String text) {
    content.setText(text);
  }


  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateOptionText(@NonNull Location location) {
    optionContent.setVisibility(View.VISIBLE);
    optionContent.setText("Latitude:" + location.getLatitude() + "  Longitude:" + location.getLongitude()
        + " Accuracy:" + location.getAccuracy());
  }
  
  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  @UiThread  void handlePostDetach() {
    Intent intent = new Intent(context, BackgroundLocationService.class);
    context.stopService(intent);
  }


  public HashMap<String, GPSDto> getData() throws JSONException {
    GPSDto aDto = new GPSDto();
    aDto.setEnabled(isEnabled);
    aDto.setAccuracyMode(accuracyMode);
    aDto.setAvailable(isAvailable);
    aDto.setProvider(provider);
    aDto.setLocation(location);

    HashMap<String, GPSDto> hashMap = new HashMap<>(1);
    hashMap.put("gps", aDto);
    return hashMap;// Constants.GSON.toJson(hashMap);
  }
}