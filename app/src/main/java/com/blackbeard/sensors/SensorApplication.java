package com.blackbeard.sensors;

import android.app.Application;
import okhttp3.OkHttpClient;

/**
 * Created by sudendra.kamble on 22/10/16.
 */

public class SensorApplication extends Application {

  private final OkHttpClient client = new OkHttpClient();


  @Override public void onCreate() {
    super.onCreate();
  }

  public OkHttpClient getClient() {
    return client;
  }
}
