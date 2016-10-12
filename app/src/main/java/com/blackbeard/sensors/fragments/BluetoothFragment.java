package com.blackbeard.sensors.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.BluetoothDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

@EFragment(R.layout.fragment_plus_one) public class BluetoothFragment extends Fragment {
  public static final String TAG = BluetoothFragment.class.getSimpleName();

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;

  @ViewById ProgressBar progressBar;

  private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
  private ArrayList<String> devicesList = new ArrayList<>(5);
  String status;
  String bluetoothOnStatus;

  @AfterViews  void init() {
    title.setText("Bluetooth");
    adapter.startDiscovery();
    updateText("Status:" + (adapter.isEnabled()?"on":"off"));
  }

  private void extractAndUpdate(Intent intent) {
    final String action = intent.getAction();
    String text = "Status:";
    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
      final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
      switch (state) {
        case BluetoothAdapter.STATE_OFF:
          status += "off";
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          status += "switching-off";
          break;
        case BluetoothAdapter.STATE_ON:
          status += "on";
          break;
        case BluetoothAdapter.STATE_TURNING_ON:
          status += "switching-on";
          break;
      }
    }
    updateText(text+status);
  }

  @Receiver(actions = BluetoothAdapter.ACTION_STATE_CHANGED, registerAt = Receiver.RegisterAt.OnAttachOnDetach)
   void onBluetoothStatusChanged(Intent intent) {
    extractAndUpdate(intent);
  }

  @Receiver(actions = {BluetoothDevice.ACTION_FOUND, BluetoothAdapter.ACTION_DISCOVERY_STARTED, BluetoothAdapter.ACTION_DISCOVERY_FINISHED}, registerAt = Receiver.RegisterAt.OnAttachOnDetach)
   void onBluetoothDiscovery(Intent intent) {
    String action = intent.getAction();
    String title = "";
    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
      //discovery starts, we can show progress dialog or perform other tasks
      title = "Started searching...";
    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
      //discovery finishes, dismiss progress dialog
      title = "Search finished...";
    } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
      //bluetooth device found
      BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
      title = "Found ...";
    }
    Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
    devicesList.clear();

    if (pairedDevices.size() > 0) {
      for (BluetoothDevice device : pairedDevices) {
        String deviceBTName = device.getName();
        String deviceBTMajorClass = getBTMajorDeviceClass(device.getBluetoothClass().getMajorDeviceClass());
        devicesList.add(deviceBTName + "  :  " + deviceBTMajorClass);
      }
    } else {
      devicesList.clear();
    }
    updateOptionText(title, devicesList);
  }

  private String getBTMajorDeviceClass(int major){
    if (major == BluetoothClass.Device.Major.AUDIO_VIDEO) return "AUDIO_VIDEO";
    else if (major == BluetoothClass.Device.Major.COMPUTER) return "COMPUTER";
    else if (major == BluetoothClass.Device.Major.HEALTH) return "HEALTH";
    else if (major == BluetoothClass.Device.Major.IMAGING) return "IMAGING";
    else if (major == BluetoothClass.Device.Major.MISC) return "MISC";
    else if (major == BluetoothClass.Device.Major.NETWORKING) return "NETWORKING";
    else if (major == BluetoothClass.Device.Major.PERIPHERAL) return "PERIPHERAL";
    else if (major == BluetoothClass.Device.Major.PHONE) return "PHONE";
    else if (major == BluetoothClass.Device.Major.TOY) return "TOY";
    else if (major == BluetoothClass.Device.Major.UNCATEGORIZED) return "UNCATEGORIZED";
    else if (major == BluetoothClass.Device.Major.WEARABLE) return "AUDIO_VIDEO";
    else return "unknown!";
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateText(String text) {
    content.setText(text);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)  void updateOptionText(String text, ArrayList<String> list) {
    optionContent.setVisibility(View.VISIBLE);
    bluetoothOnStatus = text;
    optionContent.setText(text + "\n" + (list.size() > 0 ? list.toString() : "No devices"));
  }


  @Override public void onDetach() {
    handlePostDetach();
    super.onDetach();
  }

  @UiThread  void handlePostDetach() {
    adapter.cancelDiscovery();
  }


  public HashMap<String, BluetoothDto> getData() throws JSONException {
    BluetoothDto bDto = new BluetoothDto();
    bDto.setBluetoothOnStatus(bluetoothOnStatus);
    bDto.setStatus(status);
    bDto.setDevicesList(devicesList);
    HashMap<String, BluetoothDto> hashMap = new HashMap<>(1);
    hashMap.put("bluetooth", bDto);
    return  hashMap;//Constants.GSON.toJson(hashMap);
  }
}
