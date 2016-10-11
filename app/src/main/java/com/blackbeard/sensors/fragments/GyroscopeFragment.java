package com.blackbeard.sensors.fragments;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.GPSDto;
import com.blackbeard.sensors.dto.GyroDto;
import com.blackbeard.sensors.utils.Constants;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EFragment(R.layout.fragment_plus_one) public class GyroscopeFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = GyroscopeFragment.class.getSimpleName();

  @ViewById TextView title;

  @ViewById TextView content;

  @SystemService
  SensorManager sensorManager;
  Sensor senGyroscope;

  PackageManager pm;
  boolean available;
  float v1,v2,v3;

  @AfterInject  void initSensor() {
    senGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    sensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    pm = getActivity().getPackageManager();
    available = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
  }

  @AfterViews  void init() {
    title.setText("Gyroscope");
    content.setText("Available:" + (available?"yes":"no"));
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(float v1, float v2,
      float v3) {
    content.setText(String.format("Available:%s\n", available ? "yes" : "no"));
    content.append("V1:" + v1 + "  V2:" + v2 + " V3:" + v3);
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  @UiThread void handlePostDetach() {
    sensorManager.unregisterListener(this);
  }

  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;

    if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
      v1 = sensorEvent.values[0];
      v2 = sensorEvent.values[1];
      v3 = sensorEvent.values[2];
      updateText(v1,v2,v3);
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}


  public JSONObject getData() throws JSONException {
    GyroDto aDto = new GyroDto();
    aDto.setAvailable(available);
    aDto.setV1(v1);
    aDto.setV2(v2);
    aDto.setV3(v3);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("gyroscope", Constants.GSON.toJson(aDto));
    return jsonObject;
  }
}
