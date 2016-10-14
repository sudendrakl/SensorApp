package com.blackbeard.sensors.fragments;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.StepsDto;
import java.util.HashMap;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

//https://developer.android.com/guide/topics/connectivity/nfc/nfc.html
//http://www.survivingwithandroid.com/2015/03/nfc-in-android-ndef-2.html
@EFragment(R.layout.fragment_plus_one) public class StepCounterFragment extends Fragment implements SensorEventListener {

  public static final String TAG = "step_counter";

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;

  PackageManager pm;

  @SystemService SensorManager sensorManager;
  Sensor senStepCounter;
  Sensor senStepDetector;

  private boolean availableStepCounter;
  private boolean availableStepDetector;
  // Steps counted in current session
  private int mSteps = 0;
  // Value of the step counter sensor when the listener was registered.
  // (Total steps are calculated from this value.)
  private int mCounterSteps = 0;

  @AfterInject  void init() {
    pm = getActivity().getPackageManager();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      availableStepCounter = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
      availableStepDetector = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);

      senStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
      senStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

      if (availableStepCounter) sensorManager.registerListener(this, senStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
      if (availableStepDetector) sensorManager.registerListener(this, senStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
    }
  }

  @AfterViews  void initViews() {
    title.setText("StepCounter");
    updateText("Available:" + (availableStepCounter || availableStepDetector ? "yes" : "no"),
        " Enabled:" + (isEnabled() ? "yes" : "no"));
    if (isEnabled()) updateOptionText("Start walking");
  }

  private boolean isEnabled() {
    return senStepCounter != null || senStepDetector != null;
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(String text, String text2) {
    content.setText(text);
    if (text2 != null) content.append(text2);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateOptionText(String text) {
    optionContent.setVisibility(View.VISIBLE);
    optionContent.setText(text);
  }

  @Override public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
      // A step detector event is received for each step.
      // This means we need to count steps ourselves
      mSteps += event.values.length;
    } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
      /*
      A step counter event contains the total number of steps since the listener
      was first registered. We need to keep track of this initial value to calculate the
      number of steps taken, as the first value a listener receives is undefined.
       */
      if (mCounterSteps < 1) {
        // initial value
        mCounterSteps = (int) event.values[0];
      }
      // Calculate steps taken based on first counter value received.
      mSteps = (int) event.values[0] - mCounterSteps;
    }
    updateOptionText("Steps:"+mSteps);
  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @UiThread  void handlePostDetach() {
    sensorManager.unregisterListener(this);
  }

  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  public HashMap<String, StepsDto> getData() throws JSONException {
    StepsDto aDto = new StepsDto();
    aDto.setAvailableStepCounter(availableStepCounter);
    aDto.setAvailableStepDetector(availableStepDetector);
    aDto.setSteps(mSteps);

    HashMap<String, StepsDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG, aDto);
    return hashMap;// Constants.GSON.toJson(hashMap);

  }
}
