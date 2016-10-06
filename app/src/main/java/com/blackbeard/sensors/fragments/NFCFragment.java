package com.blackbeard.sensors.fragments;

import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import com.bizapps.sensors.R;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

//https://developer.android.com/guide/topics/connectivity/nfc/nfc.html
//http://www.survivingwithandroid.com/2015/03/nfc-in-android-ndef-2.html
@EFragment(R.layout.fragment_plus_one) public class NFCFragment extends Fragment {
  public static final String TAG = NFCFragment.class.getSimpleName();

  @ViewById TextView title;

  @ViewById TextView content;

  @ViewById TextView optionContent;

  PackageManager pm;

  private boolean available;
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
      boolean isEnabled = false;
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

}
