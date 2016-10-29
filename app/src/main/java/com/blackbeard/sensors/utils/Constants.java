package com.blackbeard.sensors.utils;

import com.bizapps.sensors.BuildConfig;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;

/**
 * Created by sudendra.kamble on 29/09/16.
 */

public interface Constants {

  long SYNC_INITIAL_DELAY = 10000L; // 10 sec
  long SYNC_PERIOD = 30000L;// 30 sec
  long UPDATE_UI_DELAY = 1000L;

  interface URLS {
    String LOGIN = BuildConfig.BASE_URL + "api/login/";
    String REGISTER = BuildConfig.BASE_URL + "api/register/";
    String ADD_ENTRY = BuildConfig.BASE_URL + "api/add_entry/";
    String SEARCH = BuildConfig.BASE_URL + "api/search/";
    String LOGOUT = BuildConfig.BASE_URL + "api/logout";
  }

  interface SEARCH_PARAMS {
    String NAME = "user_name";
    String PHONE = "phone";
  }

  MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
  MediaType JSON_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");

  String PREFS = "shared_pref";
  String PREF_TOKEN = "token";

  String ACTION_BROADCAST_LOCATION = "com.blackbeard.broadcast.location";
  String ACTION_BROADCAST_ACCELEROMETER = "com.blackbeard.broadcast.accelerometer";

  Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
  .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  .create();

}
