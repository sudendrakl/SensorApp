package com.blackbeard.sensors.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.api.dto.ThermometerDto;
import java.util.HashMap;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

@EFragment(R.layout.fragment_plus_one) public class ThermometerFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = "thermometer";

  @ViewById TextView title;

  @ViewById TextView content;

  @SystemService
  SensorManager sensorManager;
  Sensor senTemperature;
  float ambientTemperatureCelcius;

  @AfterViews  void initViews() {
    title.setText("Thermometer");
    updateText(senTemperature != null, "no");

  }

  @AfterInject  void initSensor() {
    senTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    if (senTemperature != null) sensorManager.registerListener(this, senTemperature, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(boolean available, String v1) {
    content.setText(String.format("Available:%s Temperature:%s", available ? "yes" : "no", v1));
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }


  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;

    if (mySensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
      ambientTemperatureCelcius = sensorEvent.values[0];
      //float temperature = senTemperature.getPower();
      updateText(senTemperature != null, ambientTemperatureCelcius + "C");
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @UiThread  void handlePostDetach() {
    sensorManager.unregisterListener(this);
  }

  public HashMap<String, ThermometerDto> getData() throws JSONException {
    ThermometerDto aDto = new ThermometerDto();
    aDto.setValue(ambientTemperatureCelcius);
    aDto.setAvailable(senTemperature != null);
    HashMap<String, ThermometerDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG, aDto);
    return hashMap;//Constants.GSON.toJson(hashMap);
  }
}
