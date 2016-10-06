package com.blackbeard.sensors.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_plus_one) public class BatteryFragment extends Fragment {
  public static final String TAG = BatteryFragment.class.getSimpleName();
  private final static String BATTERY_LEVEL = "level";

  @ViewById TextView title;

  @ViewById TextView content;

  @AfterViews  void init() {
    title.setText("Battery");
    initSensor();
  }

  private void initSensor() {
    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent intent = getActivity().registerReceiver(null, ifilter);
    assert intent != null;
    extractAndUpdate(intent);
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
    content.setText("Charging:"+isCharging +" Type:"+chargingType+"\nAvailable:" + level + "%");
  }

}