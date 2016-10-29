package com.blackbeard.sensors.ui.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.api.dto.APIResponseDto;
import com.blackbeard.sensors.api.dto.AccelerometerDto;
import com.blackbeard.sensors.api.dto.BarometerDto;
import com.blackbeard.sensors.api.dto.BatteryDto;
import com.blackbeard.sensors.api.dto.BluetoothDto;
import com.blackbeard.sensors.api.dto.DeviceInfoDto;
import com.blackbeard.sensors.api.dto.GPSDto;
import com.blackbeard.sensors.api.dto.GyroDto;
import com.blackbeard.sensors.api.dto.NFCDto;
import com.blackbeard.sensors.api.dto.ProximityDto;
import com.blackbeard.sensors.api.dto.SearchResultDto;
import com.blackbeard.sensors.api.dto.StepsDto;
import com.blackbeard.sensors.api.dto.ThermometerDto;
import com.blackbeard.sensors.fragments.AccelerometerFragment;
import com.blackbeard.sensors.fragments.BarometerFragment;
import com.blackbeard.sensors.fragments.BatteryFragment;
import com.blackbeard.sensors.fragments.BluetoothFragment;
import com.blackbeard.sensors.fragments.GPSFragment;
import com.blackbeard.sensors.fragments.GyroscopeFragment;
import com.blackbeard.sensors.fragments.NFCFragment;
import com.blackbeard.sensors.fragments.ProximityFragment;
import com.blackbeard.sensors.fragments.StepCounterFragment;
import com.blackbeard.sensors.fragments.ThermometerFragment;
import com.blackbeard.sensors.ui.adapter.SearchAdapter;
import com.blackbeard.sensors.utils.AppUtil;
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.PreferencesUtil;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

  private static final String TAG = SearchActivity.class.getSimpleName();
  
  Toolbar toolbar;
  SearchView searchView;
  private final OkHttpClient client = new OkHttpClient();
  private RecyclerView recyclerView;
  private SearchAdapter searchAdapter = new SearchAdapter(new ArrayList<DeviceInfoDto>());
  private ProgressDialog progressDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    //actionBar.setDisplayHomeAsUpEnabled(true);
    //actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(false);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        SearchActivity.this.onBackPressed();
      }
    });

    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    LinearLayoutManager ll = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(ll);
    recyclerView.setAdapter(searchAdapter);
    progressDialog = new ProgressDialog(this);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
  }

  void showProgressDialog(){
    progressDialog.setMessage("Searching...");
    progressDialog.show();
  }

  void hideProgressDialog(){
    progressDialog.hide();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_search, menu);
    // Associate searchable configuration with the SearchView
    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    MenuItem searchItem = menu.findItem(R.id.action_search);
    searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setIconifiedByDefault(false);
    searchView.setIconified(false);

    searchView.setOnQueryTextListener(this);
    searchView.setMaxWidth(Integer.MAX_VALUE);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    // this is your adapter that will be filtered
    return true;

  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    // this is your adapter that will be filtered
    AppUtil.hideKeyBoard(searchView);
    showProgressDialog();
    if (!TextUtils.isEmpty(query.trim())) {
      AsyncTask<String, Void, Object[]> asyncTask = new AsyncTask<String, Void, Object[]>() {
        @Override protected void onPreExecute() {
          showProgressDialog();
        }

        @Override protected Object[] doInBackground(String[] params) {
          String responseBody = null;
          Response response = null;
          try {
            String searchParam;
            if(Patterns.PHONE.matcher(params[0]).matches())
              searchParam = Constants.SEARCH_PARAMS.PHONE;
            else searchParam = Constants.SEARCH_PARAMS.NAME;

            HashMap<String, String[]> searchParams = new HashMap<>(2);
            searchParams.put("search_params", new String[] { searchParam });//TODO: change this to simple 1 query thing
            searchParams.put("search_values", new String[] { params[0] });
            response = sendRequest(Constants.URLS.SEARCH, Constants.GSON.toJson(searchParams));

            responseBody = new String(response.body().bytes());

          } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Some shit happened....", e);
          }
          return new Object[] { response, responseBody };
        }

        @Override protected void onPostExecute(Object[] responses) {
          hideProgressDialog();
          Response response = ((Response) responses[0]);
          String responseBody = (String) responses[1];
          try {
            if(response==null || responseBody == null) {
              handleFailure(response, responseBody);
              return;
            }
            Log.d(TAG, "Result --- " + responseBody);
            if (!response.isSuccessful()) {
              handleFailure(response, responseBody);
            } else {
              handleSuccess(response, responseBody);
            }
          } catch (IOException | JsonSyntaxException ex) {
            Log.e(TAG, "Some shit happened...." + responseBody, ex);
          }
        }

        @Override protected void onCancelled() {
          super.onCancelled();
          hideProgressDialog();
        }
      };
      asyncTask.execute(query.trim());
    }
    return true;
  }


  private Response sendRequest(String url, String postParams)
      throws IOException {

    RequestBody body = RequestBody.create(Constants.JSON_TYPE_MARKDOWN, postParams);
    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", PreferencesUtil.getToken(this))
        .post(body)
        .build();

    Response response = client.newCall(request).execute();

    return response;
  }


  void handleFailure(final Response response, final String responseBody) throws IOException, JsonSyntaxException {
    if(response==null || responseBody == null) {
      Snackbar.make(searchView, "Failed to search, please check n/w", Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", null)
          .show();
      searchAdapter.refresh(null);
      return;
    }
    runOnUiThread(new Runnable() {
      @Override public void run() {
        int responseCode = response.code();
        Log.d(TAG, String.format("handleFailure(%d): %s", responseCode, responseBody));
        APIResponseDto responseParse = Constants.GSON.fromJson(responseBody, APIResponseDto.class);
        if (responseCode == 400 || responseCode == 500) {
          Snackbar.make(toolbar, responseParse.getMessage(), Snackbar.LENGTH_SHORT)
              .setAction("OK", null)
              .show();
        } else if (responseCode == 401 || responseCode == 403) { //unauthorised //clear token, redirect to login from here
          Snackbar.make(toolbar, "Unauthorised access", Snackbar.LENGTH_SHORT)
              .show();
          PreferencesUtil.saveToken(SearchActivity.this, null);
          finish();
          startActivity(new Intent(SearchActivity.this, LoginActivity.class));
        } else {
          //Snackbar.make(toolbar, "Unknown server error", Snackbar.LENGTH_INDEFINITE)
          //    .setAction("OK", null)
          //    .show();
        }
      }
    });
  }

  void handleSuccess(Response response, String responseBody) throws IOException {
    SearchResultDto responseParse = Constants.GSON.fromJson(responseBody, SearchResultDto.class);
    Log.i(TAG, response.toString());

    if(responseParse.isStatus()) {
      Log.d(TAG, String.format("handleSuccess: %s", responseParse.getMessage()));
      //Snackbar.make(searchView, responseParse.getMessage(), Snackbar.LENGTH_LONG).setAction("OK", null).show();
      parseHardwareDetails(responseParse.getResponse());

      searchAdapter.refresh(responseParse.getResponse());

    } else {
      handleFailure(response, responseBody);
    }
  }

  private void parseHardwareDetails(ArrayList<DeviceInfoDto> response) {
    for (DeviceInfoDto deviceInfoDto : response) {
      List list = new ArrayList<>();
      for (Object object : deviceInfoDto.getHardwareDetails()) {
        LinkedTreeMap map = (LinkedTreeMap) object;
        String key = (String) map.keySet().toArray()[0];
        final Class clazz = getTypeParse(key);
        try {
          list.add(0,Constants.GSON.fromJson(map.get(key).toString(), clazz));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      deviceInfoDto.getHardwareDetails().clear();
      deviceInfoDto.getHardwareDetails().addAll(list);
    }
  }

  private Class getTypeParse(String type) {
    switch (type) {
      case AccelerometerFragment.TAG:return AccelerometerDto.class;
      case BarometerFragment.TAG:return BarometerDto.class;
      case BatteryFragment.TAG:return BatteryDto.class;
      case BluetoothFragment.TAG:return BluetoothDto.class;
      case GPSFragment.TAG:return GPSDto.class;
      case GyroscopeFragment.TAG:return GyroDto.class;
      case NFCFragment.TAG:return NFCDto.class;
      case ProximityFragment.TAG:return ProximityDto.class;
      case StepCounterFragment.TAG:return StepsDto.class;
      case ThermometerFragment.TAG:return ThermometerDto.class;
    }
    return Object.class;
  }

  @Override protected void onStop() {
    hideProgressDialog();
    super.onStop();
  }
}
