package com.blackbeard.sensors.utils;

import android.content.Context;
import com.blackbeard.sensors.SensorApplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sudendra.kamble on 19/10/16.
 * refer https://github.com/square/okhttp/wiki/Recipes
 * https://github.com/square/okhttp
 */


public class NetworkClass {
  Gson GSON = new Gson();


  MediaType JSON_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
  MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


  public void testPostAPI(Context context) {

    try {
      JSONObject postParams = new JSONObject();
      postParams.put("name", "John");
      postParams.put("height", 6.1);
      postParams.put("auth_token", "WERT!@#$%^&DFGHJK");

      //String postParamsString = GSON.toJson(postParams);

      Response response = postRequest(context, "http://www.google.com/", postParams.toString());
      String responseString = response.body().string();
      if(response.isSuccessful()) {
        handleSuccess(response, responseString);
      } else {
        handleFailure(response, responseString);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }


  public SuccessAPIResponseDto testGetAPI(Context context) {
    try {
      Response response = getRequest(context, "http://www.google.com/");
      String responseString = response.body().string();
      if(response.isSuccessful()) {
        return handleSuccess(response, responseString);
      } else {
        handleFailure(response, responseString);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Response getRequest(Context context, String url) throws IOException {
    OkHttpClient client = ((SensorApplication)context.getApplicationContext()).getClient();
    Request request = new Request.Builder()
        .url(url)
       // .header("Authorization", "token")  //extra headers.... or addHeader()
        .get()
        .build();

    Call call = client.newCall(request);
    Response response = call.execute();

    return response;
  }

  private Response postImage(Context context, String url, String imagePath) throws IOException {
    OkHttpClient client = ((SensorApplication)context.getApplicationContext()).getClient();

    RequestBody postMultipartData = RequestBody.create(MEDIA_TYPE_PNG, new File(imagePath));
    RequestBody requestBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("title", "Square Logo")
        .addFormDataPart("image", "logo-square.png", postMultipartData)
        .build();

    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", "token")  //extra headers.... or addHeader()
        .post(requestBody)
        .build();

    Response response = client.newCall(request).execute();

    return response;
  }

  private Response postRequest(Context context, String url, String postParams) throws IOException {

    OkHttpClient client = ((SensorApplication)context.getApplicationContext()).getClient();
    RequestBody body = RequestBody.create(JSON_TYPE_MARKDOWN, postParams);
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
    if(response == null || responseBody == null) { //no data connected or wifi
      return;
    }

    FailureAPIResponseDto responseParse = GSON.fromJson(responseBody, FailureAPIResponseDto.class);
    if (response.code() == 400 || response.code() == 500) {
      //handle error codes
    } else if (response.code() == 401) {
      //api error
    } else {
      //
    }
  }

  private SuccessAPIResponseDto handleSuccess(Response response, String responseBody) throws IOException {
    SuccessAPIResponseDto responseParse = GSON.fromJson(responseBody, SuccessAPIResponseDto.class);

    if(responseParse.status) {
      //do something here, update ui
    } else {
      handleFailure(response, responseBody);
    }
    return responseParse;
  }

  public class FailureAPIResponseDto {
    boolean status; //give name of response fields as variable
    String message;
    int code;
  }

  public class SuccessAPIResponseDto {
    boolean status;
    String message;
    String manufacturer;
    String model;
    String system_version;
    String system_version_name;
    String uid;
    String user_name;
    String phone;
  }
}
