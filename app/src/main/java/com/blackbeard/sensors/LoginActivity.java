package com.blackbeard.sensors;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
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
import com.bizapps.sensors.R;
import com.blackbeard.sensors.dto.RegisterDto;
import com.blackbeard.sensors.dto.TokenDto;
import com.blackbeard.sensors.utils.AppUtil;
import com.blackbeard.sensors.utils.Constants;
import com.blackbeard.sensors.utils.PreferencesUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

  /**
   * Id to identity READ_CONTACTS permission request.
   */
  private static final int REQUEST_READ_CONTACTS = 0;
  private static final String TAG = LoginActivity.class.getSimpleName();
  private static final int PERMISSION_REQUEST_CODE = 123;

  private UserLoginTask mAuthTask = null;

  // UI references.
  private AutoCompleteTextView mPhoneView;
  private AutoCompleteTextView mNameView;
  private AppCompatTextView mRegisterView;
  private AutoCompleteTextView mPasswordView;

  private View mProgressView;
  private View mLoginFormView;
  private Button mEmailSignInButton;
  private boolean isLogin = true;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // Set up the login form.
    mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone);
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

    mPasswordView = (AutoCompleteTextView) findViewById(R.id.password);

    mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
    mEmailSignInButton.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        attemptLogin();
      }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
    mRegisterView = (AppCompatTextView) findViewById(R.id.register);
    mRegisterView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        toggleRegister();
      }
    });
  }

  private void toggleRegister() {
    isLogin = !isLogin;
    if(isLogin) {
      mNameView.setVisibility(View.GONE);
      mEmailSignInButton.setText(R.string.action_sign_in);
      mRegisterView.setText(R.string.action_register);
    } else {
      mNameView.setVisibility(View.VISIBLE);
      mEmailSignInButton.setText(R.string.action_register);
      mRegisterView.setText(R.string.action_sign_in);
    }
  }

  @Override protected void onStart() {
    super.onStart();
    String token = PreferencesUtil.getToken(this);
    if (!TextUtils.isEmpty(token)) {
      finish();
      Intent intent = new Intent(this, MainActivity.class);
      LoginActivity.this.startActivity(intent);
    }

    checkPermissionShit();
  }

  private boolean checkPermissionShit() {
    // Here, thisActivity is the current activity
    String permissionArray[] = new String[] {
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH, Manifest.permission.NFC,
    };
    for (final String permission : permissionArray) {
      if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
          Snackbar.make(mNameView, "Please grant permission in settings", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override public void onClick(View v) {
              Log.i(TAG, "Grant permission in settings");
              ActivityCompat.requestPermissions(LoginActivity.this, new String[] { permission },
                  PERMISSION_REQUEST_CODE);
            }
          }).show();
          return false;
        } else {
          ActivityCompat.requestPermissions(this, permissionArray, PERMISSION_REQUEST_CODE);
          break;
        }
      }
    }
    return true;
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
    final String contactPermission = Manifest.permission.READ_CONTACTS;
    if (checkSelfPermission(contactPermission) == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    if (shouldShowRequestPermissionRationale(contactPermission)) {
      Snackbar.make(mPhoneView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
          .setAction(android.R.string.ok, new View.OnClickListener() {
            @Override @TargetApi(Build.VERSION_CODES.M) public void onClick(View v) {
              ActivityCompat.requestPermissions(LoginActivity.this, new String[] { contactPermission }, REQUEST_READ_CONTACTS);
            }
          });
    } else {
      ActivityCompat.requestPermissions(this, new String[] { contactPermission }, REQUEST_READ_CONTACTS);
    }
    return false;
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case REQUEST_READ_CONTACTS: {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          populateAutoComplete();
        }
        break;
      }
      case PERMISSION_REQUEST_CODE: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          // permission was granted, yay! Do the
          // contacts-related task you need to do.

        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
        }
        break;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  private void attemptLogin() {
    if(!checkPermissionShit())
      return;
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mPhoneView.setError(null);
    mNameView.setError(null);

    // Store values at the time of the login attempt.
    String phone = mPhoneView.getText().toString();
    String name = mNameView.getText().toString().trim();
    String password = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid name, if the user entered one.
    if (!isLogin && (TextUtils.isEmpty(name) || !isNameValid(name))) {
      mNameView.setError(getString(R.string.error_invalid_name));
      focusView = mNameView;
      cancel = true;
    }

    if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    // Check for a valid phone address.
    if (TextUtils.isEmpty(phone)) {
      mPhoneView.setError(getString(R.string.error_field_required));
      focusView = mPhoneView;
      cancel = true;
    } else if (!isPhoneValid(phone)) {
      mPhoneView.setError(getString(R.string.error_invalid_phone));
      focusView = mPhoneView;
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
      mAuthTask = new UserLoginTask(phone, name, password);
      mAuthTask.execute((Void) null);
    }
  }

  private boolean isEmailValid(String email) {
    return email.contains("@");
  }

  private boolean isPhoneValid(String phone) {
    return phone.length()==10;
  }

  private boolean isPasswordValid(String password) {
    return password.length()>3 && password.length()<12;
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

    mPhoneView.setAdapter(adapter);
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
  public class UserLoginTask extends AsyncTask<Void, Void, Response> {

    private final String phone;
    private final String name;
    private final OkHttpClient client = new OkHttpClient();

    private final Gson gson;
    private final String password;

    UserLoginTask(String phone, String name, String password) {
      this.phone = phone;
      this.name = name;
      this.gson = Constants.GSON;
      this.password = password;

  }

    @Override protected Response doInBackground(Void... params) {
      try {

        String jsonParams;
        String url;
        String imei = AppUtil.getImeiOrUniqueID(LoginActivity.this);
        if (!isLogin) {
          jsonParams = gson.toJson(new RegisterDto(imei, phone, name, password));
          url = Constants.URLS.REGISTER;
        } else {
          jsonParams = gson.toJson(new RegisterDto(imei, phone, null, password));
          url = Constants.URLS.LOGIN;
        }
        return sendRequest(url, jsonParams);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    private Response sendRequest(String url, String postParams)
        throws IOException {
      RequestBody body = RequestBody.create(Constants.JSON_TYPE_MARKDOWN, postParams);
      Request request = new Request.Builder()
          .url(url)
          .post(body)
          .build();

      Response response = client.newCall(request).execute();

      return response;
    }

    @Override protected void onPostExecute(final Response response) {
      mAuthTask = null;
      showProgress(false);
      try {
        if (!response.isSuccessful()) {
          handleFailure(response);
        } else {
          handleSuccess(response);
        }
      } catch (IOException ex) {
        Log.e(TAG, "Some shit happened", ex);
      }
    }

    @Override protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }

    void handleFailure(Response response) throws IOException {
      TokenDto responseParse = gson.fromJson(new String(response.body().bytes()), TokenDto.class);
      handleFailure(response, responseParse);
    }

    void handleFailure(final Response response, final TokenDto tokenDto) {
      runOnUiThread(new Runnable() {
        @Override public void run() {
          if (!isLogin) { //registration
            if (tokenDto.getCode() == 1) {
              Snackbar.make(mLoginFormView, tokenDto.getMessage(), Snackbar.LENGTH_SHORT)
                  .setAction("OK", null)
                  .show();
            } else {
              Snackbar.make(mLoginFormView, tokenDto.getMessage() + ". Retry again", Snackbar.LENGTH_LONG).setAction("OK", null).show();
            }
          } else { //login
            switch (response.code()) {
              case 200:
              case 501:
                Snackbar.make(mLoginFormView, tokenDto.getMessage(), Snackbar.LENGTH_LONG).show();
                mPasswordView.requestFocus();
                break;
              case 400:
                Snackbar.make(mLoginFormView, tokenDto.getMessage(), Snackbar.LENGTH_LONG).show();
                break;
              case 403:
                Snackbar.make(mLoginFormView, tokenDto.getMessage(), Snackbar.LENGTH_LONG).show();
                mPhoneView.requestFocus();
                break;
            }
          }
        }
      });
    }

    void handleSuccess(Response response) throws IOException {
      TokenDto responseParse = gson.fromJson(new String(response.body().bytes()), TokenDto.class);
      Log.i(TAG, response.toString());

      if(responseParse.isStatus()) {
        PreferencesUtil.saveToken(LoginActivity.this, responseParse.getToken());
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
      } else {
        handleFailure(response, responseParse);
      }
    }
  }
}

