package com.blackbeard.sensors.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.AccelerometerDto;
import com.blackbeard.sensors.utils.Constants;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EFragment(R.layout.fragment_plus_one) public class AccelerometerFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = AccelerometerFragment.class.getSimpleName();

  @ViewById TextView title;

  @ViewById TextView content;

  @SystemService
  SensorManager sensorManager;
  Sensor senAccelerometer;
  float x,y,z;
  boolean isEnabled;

  @AfterViews  void init() {
    title.setText("Accelerometer");
  }

  @AfterInject  void initSensor() {
    senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(float x, float y,
      float z) {
    content.setText("X:" + x + "  Y:" + y + " Z:" + z);
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  @UiThread  void handlePostDetach() {
    sensorManager.unregisterListener(this);
  }

  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;
    isEnabled = true;
    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
       x = sensorEvent.values[0];
       y = sensorEvent.values[1];
       z = sensorEvent.values[2];
      updateText(x,y,z);
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  public JSONObject getData() throws JSONException {
    AccelerometerDto aDto = new AccelerometerDto();
    aDto.setX(x);
    aDto.setZ(y);
    aDto.setZ(z);
    aDto.setAvailable(senAccelerometer!=null);
    aDto.setEnabled(isEnabled);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("accelerometer", Constants.GSON.toJson(aDto));
    return jsonObject;
  }
}
