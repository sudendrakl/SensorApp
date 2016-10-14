package com.blackbeard.sensors.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.BarometerDto;
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

@EFragment(R.layout.fragment_plus_one) public class BarometerFragment extends Fragment implements
    SensorEventListener{
  public static final String TAG = "barometer";

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;

  @SystemService
  SensorManager sensorManager;
  Sensor senPressure;
  float pressure;
  boolean isEnabled;
  Timer timer;
  TimerTask timerTask;

  @AfterViews  void init() {
    title.setText("Barometer");
    updateText("Available:" + (senPressure != null ? "yes" : "no"));
    if (senPressure != null) timer.scheduleAtFixedRate(timerTask, 0, Constants.UPDATE_UI_DELAY);
  }

  @AfterInject  void initSensor() {
    senPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    sensorManager.registerListener(this, senPressure, SensorManager.SENSOR_DELAY_NORMAL);
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override public void run() {
        updateOptionText();
      }
    };
  }

  @UiThread(propagation = UiThread.Propagation.REUSE) void updateText(String text) {
    content.setText(text);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateOptionText() {
    optionContent.setVisibility(View.VISIBLE);
    optionContent.setText(String.format(Locale.ENGLISH, "Pressure:%.2f", pressure));
  }

  @Override public void onDetach() {
    handlePostDetach();
    timer.cancel();
    super.onDetach();
  }


  @Override public void onSensorChanged(SensorEvent sensorEvent) {
    Sensor mySensor = sensorEvent.sensor;
    isEnabled = true;
    if (mySensor.getType() == Sensor.TYPE_PRESSURE) {
      pressure = sensorEvent.values[0];
      //INFO: ui updated with timer task
    }
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @UiThread  void handlePostDetach() {
    sensorManager.unregisterListener(this);
    timer.cancel();
  }


  public HashMap<String, BarometerDto> getData() throws JSONException {
    BarometerDto bDto = new BarometerDto();
    bDto.setAvailable(senPressure!=null);
    bDto.setEnabled(isEnabled);
    bDto.setPressure(pressure);
    HashMap<String, BarometerDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG,bDto);
    return  hashMap;//Constants.GSON.toJson(hashMap);
  }
}
