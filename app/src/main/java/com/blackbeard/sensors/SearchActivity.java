package com.blackbeard.sensors;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.APIResponseDto;
import com.blackbeard.sensors.dto.DeviceInfoDto;
import com.blackbeard.sensors.dto.SearchResultDto;
import com.blackbeard.sensors.utils.AppUtil;
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.PreferencesUtil;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
  public boolean onQueryTextSubmit(final String query) {
    // this is your adapter that will be filtered
    AppUtil.hideKeyBoard(searchView);
    if (!TextUtils.isEmpty(query)) {
      new Thread(new Runnable() {
        @Override public void run() {
          try {
            HashMap<String, String[]> params = new HashMap<>(2);
            params.put("search_params", new String[]{query});
            params.put("search_values", new String[]{query});
            sendRequest(Constants.URLS.SEARCH, Constants.GSON.toJson(query));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
    return true;
  }


  private void sendRequest(String url, String postParams)
      throws IOException {

    RequestBody body = RequestBody.create(Constants.JSON_TYPE_MARKDOWN, postParams);
    Request request = new Request.Builder()
        .url(url)
        .header("Authorization", PreferencesUtil.getToken(this))
        .post(body)
        .build();

    Response response = client.newCall(request).execute();
    String responseBody = new String(response.body().bytes());
    try {
      if (!response.isSuccessful()) {
        handleFailure(response,responseBody);
      } else {
        handleSuccess(response,responseBody);
      }
    } catch (IOException|JsonSyntaxException ex) {
      Log.e(TAG, "Some shit happened...." + responseBody, ex);
    }
  }


  void handleFailure(Response response, String responseBody) throws IOException, JsonSyntaxException {
    APIResponseDto responseParse =
        Constants.GSON.fromJson(responseBody, APIResponseDto.class);
    if (response.code() == 400 || response.code() == 500) {
      Snackbar.make(searchView, responseParse.getMessage(), Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", null)
          .show();
    } else if (response.code() == 401) {
      Snackbar.make(searchView, "Unauthorised", Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", null)
          .show();
    } else {
      Snackbar.make(searchView, "Unknown server error", Snackbar.LENGTH_INDEFINITE)
          .setAction("OK", null)
          .show();
    }
  }

  void handleSuccess(Response response, String responseBody) throws IOException {
    SearchResultDto responseParse = Constants.GSON.fromJson(responseBody, SearchResultDto.class);
    //TODO: updated response
    Log.i(TAG, response.toString());

    if(responseParse.isStatus()) {
      //TODO: what to do
      Snackbar.make(searchView, responseParse.getMessage(), Snackbar.LENGTH_LONG).setAction("OK", null).show();
      searchAdapter.refresh(responseParse.getResponse());

    } else {
      handleFailure(response, responseBody);
    }
  }

}
