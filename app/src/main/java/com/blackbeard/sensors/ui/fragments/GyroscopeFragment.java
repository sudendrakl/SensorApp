package com.blackbeard.sensors.fragments;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.api.dto.GyroDto;
import com.blackbeard.sensors.utils.Constants;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

@EFragment(R.layout.fragment_plus_one) public class GyroscopeFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = "gyroscope";

  @ViewById TextView title;

  @ViewById TextView content;

  @SystemService
  SensorManager sensorManager;
  Sensor senGyroscope;

  PackageManager pm;
  boolean available;
  float v1,v2,v3;

  Timer timer;
  TimerTask timerTask;

  @AfterInject  void initSensor() {
    senGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    sensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    pm = getActivity().getPackageManager();
    available = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override public void run() {
        updateText();
      }
    };
  }

  @AfterViews  void initViews() {
    title.setText("Gyroscope");
    content.setText(String.format("Available:%s", available ? "yes" : "no"));
    timer.scheduleAtFixedRate(timerTask, 0, Constants.UPDATE_UI_DELAY);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText() {
    content.setText(String.format("Available:%s\n", available ? "yes" : "no"));
    content.append(String.format(Locale.ENGLISH, "V1:%.2f  V2:%.2f V3:%.2f", v1, v2, v3));
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  @UiThread void handlePostDetach() {
    sensorManager.unregisterListener(this);
    timer.cancel();
  }

  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;

    if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
      v1 = sensorEvent.values[0];
      v2 = sensorEvent.values[1];
      v3 = sensorEvent.values[2];
      //INFO: ui updated with timer task
      // updateText(v1,v2,v3);
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}


  public HashMap<String, GyroDto> getData() throws JSONException {
    GyroDto aDto = new GyroDto();
    aDto.setAvailable(available);
    aDto.setV1(v1);
    aDto.setV2(v2);
    aDto.setV3(v3);

    HashMap<String, GyroDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG, aDto);
    return hashMap;// Constants.GSON.toJson(hashMap);
  }
}
