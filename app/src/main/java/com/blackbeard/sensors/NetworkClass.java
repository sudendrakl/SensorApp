package com.blackbeard.sensors;

import com.blackbeard.sensors.utils.Constants;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sudendra.kamble on 19/10/16.
 */

public class NetworkClass {
  private final OkHttpClient client = new OkHttpClient();
  Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .create();


  private void testPostAPI() {
    HashMap postParams = new HashMap();
    postParams.put("name", "John");
    postParams.put("height", 6.1);
    postParams.put("auth_token", "WERT!@#$%^&DFGHJK");
    String postParamsString = GSON.toJson(postParams);
    try {
      Response response = postRequest("http://www.google.com/", postParamsString);
      String responseString = response.body().string();
      if(response.isSuccessful()) {
        handleSuccess(response, responseString);
      } else {
        handleFailure(response, responseString);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void testGetAPI() {
    try {
      Response response = getRequest("http://www.google.com/");
      String responseString = response.body().string();
      if(response.isSuccessful()) {
        handleSuccess(response, responseString);
      } else {
        handleFailure(response, responseString);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Response getRequest(String url) throws IOException {
    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", "token")  //extra headers.... or addHeader()
        .build();

    Response response = client.newCall(request).execute();

    return response;
  }

  private Response postRequest(String url, String postParams) throws IOException {

    RequestBody body = RequestBody.create(Constants.JSON_TYPE_MARKDOWN, postParams);
    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", "token")  //extra headers.... or addHeader()
        .post(body)
        .build();

    Response response = client.newCall(request).execute();

    return response;
  }


  private void handleFailure(Response response, String responseBody) throws IOException,
      JsonSyntaxException {
    if(response==null || responseBody == null) {
      return;
    }

    FailureAPIResponseDto responseParse = GSON.fromJson(responseBody, FailureAPIResponseDto.class);
    if (response.code() == 400 || response.code() == 500) {
      //handle error codes
    } else if (response.code() == 401) {
      //
    } else {
      //
    }
  }

  private void handleSuccess(Response response, String responseBody) throws IOException {
    SuccessAPIResponseDto responseParse = GSON.fromJson(responseBody, SuccessAPIResponseDto.class);

    if(responseParse.status) {
      //do something here, update ui
    } else {
      handleFailure(response, responseBody);
    }
  }

  public class FailureAPIResponseDto {
    boolean status; //give name of response fields as variable
    String message;
    int code;
  }

  public class SuccessAPIResponseDto {
    int code;
    boolean status;
    ArrayList<String> list; // for array
  }
}
