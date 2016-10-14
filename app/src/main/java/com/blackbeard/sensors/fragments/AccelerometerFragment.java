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

@EFragment(R.layout.fragment_plus_one) public class AccelerometerFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = "accelerometer";

  @ViewById TextView title;

  @ViewById TextView content;

  @SystemService
  SensorManager sensorManager;
  Sensor senAccelerometer;
  float x,y,z;
  boolean isEnabled;
  Timer timer;
  TimerTask timerTask;

  @AfterViews  void init() {
    title.setText("Accelerometer");
    timer.scheduleAtFixedRate(timerTask, 0, Constants.UPDATE_UI_DELAY);
  }

  @AfterInject  void initSensor() {
    senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override public void run() {
        updateText();
      }
    };
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText() {
    content.setText(String.format(Locale.ENGLISH, "X:%.2f  Y:%.2f Z:%.2f", x, y, z));
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  @UiThread  void handlePostDetach() {
    sensorManager.unregisterListener(this);
    timer.cancel();
  }

  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;
    isEnabled = true;
    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
       x = sensorEvent.values[0];
       y = sensorEvent.values[1];
       z = sensorEvent.values[2];
      //INFO: ui updated with timer task
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  public HashMap<String, AccelerometerDto> getData() throws JSONException {
    AccelerometerDto aDto = new AccelerometerDto();
    aDto.setX(x);
    aDto.setZ(y);
    aDto.setZ(z);
    aDto.setAvailable(senAccelerometer!=null);
    aDto.setEnabled(isEnabled);
    HashMap<String, AccelerometerDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG,aDto);
    return  hashMap;
  }
}
