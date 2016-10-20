package com.blackbeard.sensors.api.requests;

import okhttp3.Request;
import retrofit2.Retrofit;

/**
 * Created by sudendra.kamble on 20/10/16.
 */

public class ApiRequest {
  private static Retrofit retrofit;
  public static Retrofit getInstance() {
    if(retrofit == null) {
      retrofit = new Retrofit.Builder().build();
    }
    return retrofit;
  }
}
