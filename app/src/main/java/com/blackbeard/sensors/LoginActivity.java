package com.blackbeard.sensors;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import com.blackbeard.sensors.dto.RegisterDto;
import com.blackbeard.sensors.dto.TokenDto;
import com.blackbeard.sensors.utils.AppUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.blackbeard.sensors.utils.Constants;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

  /**
   * Id to identity READ_CONTACTS permission request.
   */
  private static final int REQUEST_READ_CONTACTS = 0;
  private static final String TAG = LoginActivity.class.getSimpleName();

  private UserLoginTask mAuthTask = null;

  // UI references.
  private AutoCompleteTextView mEmailView;
  private AutoCompleteTextView mNameView;
  private View mProgressView;
  private View mLoginFormView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // Set up the login form.
    mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
    populateAutoComplete();

    mNameView = (AutoCompleteTextView) findViewById(R.id.name);
    mNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.name || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });

    Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
    mEmailSignInButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        attemptLogin();
      }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }

  @Override protected void onStart() {
    super.onStart();
    SharedPreferences pref = LoginActivity.this.getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
    String token = pref.getString(Constants.PREF_TOKEN, null);
    if (!TextUtils.isEmpty(token)) {
      finish();
      Intent intent = new Intent(Constants.ACTION_START_SENSOR_ACTIVITY);
      LoginActivity.this.startActivity(intent);
    }
  }

  private void populateAutoComplete() {
    if (!mayRequestContacts()) {
      return;
    }

    getLoaderManager().initLoader(0, null, this);
  }

  private boolean mayRequestContacts() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return true;
    }
    if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
      Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
          .setAction(android.R.string.ok, new View.OnClickListener() {
            @Override @TargetApi(Build.VERSION_CODES.M) public void onClick(View v) {
              requestPermissions(new String[] { READ_CONTACTS }, REQUEST_READ_CONTACTS);
            }
          });
    } else {
      requestPermissions(new String[] { READ_CONTACTS }, REQUEST_READ_CONTACTS);
    }
    return false;
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == REQUEST_READ_CONTACTS) {
      if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        populateAutoComplete();
      }
    }
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  private void attemptLogin() {
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mEmailView.setError(null);
    mNameView.setError(null);

    // Store values at the time of the login attempt.
    String email = mEmailView.getText().toString();
    String name = mNameView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid name, if the user entered one.
    if (!TextUtils.isEmpty(name) && !isNameValid(name)) {
      mNameView.setError(getString(R.string.error_invalid_password));
      focusView = mNameView;
      cancel = true;
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(email)) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if (!isEmailValid(email)) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);
      mAuthTask = new UserLoginTask(email, name);
      mAuthTask.execute((Void) null);
    }
  }

  private boolean isEmailValid(String email) {
    return email.contains("@");
  }

  private boolean isNameValid(String name) {
    return name.length() > 4;
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate()
          .setDuration(shortAnimTime)
          .alpha(show ? 0 : 1)
          .setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
              mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
          });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate()
          .setDuration(shortAnimTime)
          .alpha(show ? 1 : 0)
          .setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
              mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
          });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return new CursorLoader(this,
        // Retrieve data rows for the device user's 'profile' contact.
        Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
            ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

        // Select only email addresses.
        ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[] {
        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
    },

        // Show primary email addresses first. Note that there won't be
        // a primary email address if the user hasn't specified one.
        ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    List<String> emails = new ArrayList<>();
    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      emails.add(cursor.getString(ProfileQuery.ADDRESS));
      cursor.moveToNext();
    }

    addEmailsToAutoComplete(emails);
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

  }

  private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
    //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line,
            emailAddressCollection);

    mEmailView.setAdapter(adapter);
  }

  private interface ProfileQuery {
    String[] PROJECTION = {
        ContactsContract.CommonDataKinds.Email.ADDRESS,
        ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
    };

    int ADDRESS = 0;
    int IS_PRIMARY = 1;
  }

  /**
   * Represents an asynchronous login/registration task used to authenticate
   * the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, TokenDto> {

    private final String email;
    private final String name;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    UserLoginTask(String email, String name) {
      this.email = email;
      this.name = name;
    }

    @Override protected TokenDto doInBackground(Void... params) {
      // TODO: attempt authentication against a network service.

      try {

        String jsonParams = gson.toJson(
            new RegisterDto(name, email, AppUtil.getImeiOrUniqueID(LoginActivity.this)));

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        Headers headers = Headers.of(headerMap);

        TokenDto tokenDto =
            sendRequest(Constants.URLS.REGISTER, headers, jsonParams, TokenDto.class);
        return tokenDto;
      } catch (IOException e) {
        e.printStackTrace();
      }

      // TODO: register the new account here.
      return null;
    }

    private <T> T sendRequest(String url, Headers headers, String postParams, Class<T> clazz)
        throws IOException {
      Request request;
      if (headers != null) {
        request = new Request.Builder().url(url)
            .headers(headers)
            .post(RequestBody.create(Constants.MEDIA_TYPE_MARKDOWN, postParams))
            .build();
      } else {
        request = new Request.Builder().url(url)
            .post(RequestBody.create(Constants.MEDIA_TYPE_MARKDOWN, postParams))
            .build();
      }

      Response response = client.newCall(request).execute();
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

      Log.i(TAG, response.body().string());

      T responseParse = gson.fromJson(response.body().charStream(), clazz);
      //TODO: updated response
      Log.i(TAG, response.toString());

      return responseParse;
    }

    @Override protected void onPostExecute(final TokenDto success) {
      mAuthTask = null;
      showProgress(false);

      if (success != null) {
        finish();
        SharedPreferences pref = LoginActivity.this.getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
        pref.edit().putString(Constants.PREF_TOKEN, success.getToken()).apply();
        Intent intent = new Intent(Constants.ACTION_START_SENSOR_ACTIVITY);
        LoginActivity.this.startActivity(intent);
      } else {
        mNameView.setError(getString(R.string.error_incorrect_password));
        mNameView.requestFocus();
      }
    }

    @Override protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }
  }
}

