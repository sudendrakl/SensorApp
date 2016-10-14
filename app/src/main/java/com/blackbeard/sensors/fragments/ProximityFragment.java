package com.blackbeard.sensors.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.ProximityDto;
import java.util.HashMap;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

@EFragment(R.layout.fragment_plus_one) public class ProximityFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = "proximity";

  @ViewById TextView title;

  @ViewById TextView content;

  @SystemService
  SensorManager sensorManager;
  Sensor senProximity;
  float value;

  @AfterViews  void init() {
    title.setText("Proximity");
    updateText(0);
  }

  @AfterInject  void initSensor() {
    senProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    sensorManager.registerListener(this, senProximity, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(float v1) {
    content.setText(String.format("Distance:%scm", v1));
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }


  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;

    if (mySensor.getType() == Sensor.TYPE_PROXIMITY) {
      value = sensorEvent.values[0];

      updateText(value);
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @UiThread  void handlePostDetach() {
    sensorManager.unregisterListener(this);
  }

  public HashMap<String, ProximityDto> getData() throws JSONException {
    ProximityDto aDto = new ProximityDto();
    aDto.setAvailable(senProximity!=null);
    aDto.setValue(value);

    HashMap<String, ProximityDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG, aDto);
    return hashMap;// Constants.GSON.toJson(hashMap);

  }
}
