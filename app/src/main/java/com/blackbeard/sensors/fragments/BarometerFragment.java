package com.blackbeard.sensors.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.AccelerometerDto;
import com.blackbeard.sensors.dto.BarometerDto;
import com.blackbeard.sensors.utils.Constants;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EFragment(R.layout.fragment_plus_one) public class BarometerFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = BarometerFragment.class.getSimpleName();

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;

  @SystemService
  SensorManager sensorManager;
  Sensor senPressure;
  float pressure;
  boolean isEnabled;

  @AfterViews  void init() {
    title.setText("Barometer");
    updateText("Available:" + (senPressure!=null ? "yes" : "no"));

  }

  @AfterInject  void initSensor() {
    senPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    sensorManager.registerListener(this, senPressure, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE) void updateText(String text) {
    content.setText(text);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateOptionText(String text) {
    optionContent.setVisibility(View.VISIBLE);
    optionContent.setText(text);
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }


  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;
    isEnabled = true;
    if (mySensor.getType() == Sensor.TYPE_PRESSURE) {
      pressure = sensorEvent.values[0];

      updateOptionText(String.format("Pressure:%s", pressure));
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @UiThread  void handlePostDetach() {
    sensorManager.unregisterListener(this);
  }


  public JSONObject getData() throws JSONException {
    BarometerDto bDto = new BarometerDto();
    bDto.setAvailable(senPressure!=null);
    bDto.setEnabled(isEnabled);
    bDto.setPressure(pressure);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("barometer", Constants.GSON.toJson(bDto));
    return jsonObject;
  }
}
