package com.blackbeard.sensors.fragments;

import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.api.dto.NFCDto;
import java.util.HashMap;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

//https://developer.android.com/guide/topics/connectivity/nfc/nfc.html
//http://www.survivingwithandroid.com/2015/03/nfc-in-android-ndef-2.html
@EFragment(R.layout.fragment_plus_one) public class NFCFragment extends Fragment {
  public static final String TAG = "nfc";

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;

  PackageManager pm;

  private boolean available, isEnabled;
  private NfcAdapter nfcAdapter;

  @AfterInject void init() {
    pm = getActivity().getPackageManager();
    available = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
    nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
  }

  @AfterViews void initViews() {
    title.setText("NFC");
    updateText("Available:" + (available ? "yes" : "no"), null);
    if (nfcAdapter != null) {
      // device has NFC functionality
      isEnabled = false;
      try {
        isEnabled = nfcAdapter.isEnabled();
      } catch (Exception e) {
        e.printStackTrace();
      }
      updateText("Available:" + (available ? "yes" : "no"), (available ? (" Enabled:" + isEnabled) : null));
    }
  }

  @UiThread(propagation = UiThread.Propagation.REUSE) void updateText(String text, String text2) {
    content.setText(text);
    if (text2 != null) content.append(text2);
  }


  public HashMap<String, NFCDto> getData() throws JSONException {
    NFCDto aDto = new NFCDto();
    aDto.setAvailable(available);
    aDto.setEnabled(isEnabled);

    HashMap<String, NFCDto> hashMap = new HashMap<>(1);
    hashMap.put(TAG, aDto);
    return hashMap;// Constants.GSON.toJson(hashMap);

  }
}
