package com.blackbeard.sensors.ui.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.api.dto.BatteryDto;
import com.blackbeard.sensors.utils.Constants;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

@EFragment(R.layout.fragment_plus_one) public class BatteryFragment extends Fragment {
  public static final String TAG = "battery";

  @ViewById TextView title;

  @ViewById TextView content;

  String isCharging, chargingType;
  float level;
  Timer timer;
  TimerTask timerTask;

  @AfterViews  void initViews() {
    title.setText("Battery");
    initSensor();
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override public void run() {
        initSensor();
      }
    };
    timer.scheduleAtFixedRate(timerTask, 0, Constants.UPDATE_UI_DELAY);
  }

  private void initSensor() {
    if (getActivity() != null && isAdded()) {
      IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
      Intent intent = getActivity().registerReceiver(null, ifilter);
      assert intent != null;
      extractAndUpdate(intent);
    }
  }

  private void extractAndUpdate(Intent batteryStatus) {
    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

    // How are we charging?
    int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
    boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

    float batteryPct = (level / (float) scale) * 100 ;
    updateText(isCharging?"yes":"no", (usbCharge ? "usb" : acCharge ? "ac" : "none"), batteryPct);
  }

  @Receiver(actions = Intent.ACTION_BATTERY_CHANGED, registerAt = Receiver.RegisterAt.OnAttachOnDetach)
   void onBatteryChanged(Intent intent) {
    extractAndUpdate(intent);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(String isCharging, String chargingType, float level) {
    this.isCharging = isCharging;
    this.chargingType = chargingType;
    this.level = level;
    content.setText(
        String.format(Locale.ENGLISH,"Charging:%s Type:%s\nAvailable:%.0f%%", isCharging, chargingType, level));
  }

  public HashMap<String, BatteryDto> getData() throws JSONException {
    BatteryDto bDto = new BatteryDto();
    bDto.setChargingType(chargingType);
    bDto.setIsCharging(isCharging);
    bDto.setLevel(level);
    HashMap<String, BatteryDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG, bDto);
    return  hashMap;//Constants.GSON.toJson(hashMap);
  }
}
