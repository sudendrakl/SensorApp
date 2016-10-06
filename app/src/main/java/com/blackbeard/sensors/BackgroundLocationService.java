package com.blackbeard.sensors;

/**
 * Created by sudendra.kamble on 29/09/2016
 */

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import com.blackbeard.sensors.utils.AppUtil;
import com.blackbeard.sensors.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.text.DateFormat;
import java.util.Date;
import org.androidannotations.annotations.EService;

public class BackgroundLocationService extends Service
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

  private static final String TAG = BackgroundLocationService.class.getSimpleName();
  private static final long UPDATE_INTERVAL = 1000;//millis
  private static final long FASTEST_INTERVAL = UPDATE_INTERVAL / 2;//millis
  private static final float MINIMUM_DISPLACEMENT = 5;//meters
  IBinder mBinder = new LocalBinder();

  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  // Flag that indicates if a request is underway.
  private boolean mInProgress;

  private Boolean servicesAvailable = false;

  public class LocalBinder extends Binder {
    public BackgroundLocationService getService() {
      return BackgroundLocationService.this;
    }
  }

  @Override public void onCreate() {
    super.onCreate();
    mInProgress = false;
    buildGoogleApiClient();
  }

  private void buildGoogleApiClient() {

    Log.i(TAG, "Building GoogleApiClient");
    mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
    createLocationRequest();

    servicesAvailable = AppUtil.isGooglePlayServicesAvailable(this);
  }

  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();

    // Sets the desired interval for active location updates. This interval is
    // inexact. You may not receive updates at all if no location sources are available, or
    // you may receive them slower than requested. You may also receive updates faster than
    // requested if other applications are requesting location at a faster interval.
    mLocationRequest.setInterval(UPDATE_INTERVAL);

    // Sets the fastest rate for active location updates. This interval is exact, and your
    // application will never receive updates faster than this value.
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setSmallestDisplacement(MINIMUM_DISPLACEMENT);
  }

  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      Intent broadcastIntent = new Intent("check_permission");
      sendBroadcast(broadcastIntent);
      return START_STICKY;
    }

    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

      Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      if (location != null) {
        sendLocation(location);
      }
      startLocationUpdates();
    }

    if (!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress) return START_STICKY;

    if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress) {
      Log.i(TAG, "Started");
      mInProgress = true;
      mGoogleApiClient.connect();
    }

    return START_STICKY;
  }

  @Override public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override public void onDestroy() {
    // Turn off the request flag
    mInProgress = false;
    if (servicesAvailable && mGoogleApiClient != null) {
      stopLocationUpdates();
      mGoogleApiClient.disconnect();
      // Destroy the current location client
      mGoogleApiClient = null;
    }
    // Display the connection status
    // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    Log.i(TAG, DateFormat.getDateTimeInstance().format(new Date()) + ": Stopped");
    super.onDestroy();
  }

  // Define the callback method that receives location updates
  @Override public void onLocationChanged(Location locations) {
    Log.d(" Testing ", "Testing location onLocationChanged ========= " + locations.toString());
    sendLocation(locations);
  }

  protected void stopLocationUpdates() {
    Log.d(" Testing ", "Testing stopLocationUpdates ");
    if(mGoogleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
  }

  protected void startLocationUpdates() {
    // The final argument to {@code requestLocationUpdates()} is a LocationListener
    // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }

    //INFO: LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    //TODO
    Intent intent = new Intent(Constants.ACTION_BROADCAST_LOCATION);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingIntent);
  }

  /*
   * Called by Location Services when the request to connect the
   * client finishes successfully. At this point, you can
   * request the current location or start periodic updates
   */
  @Override public void onConnected(Bundle bundle) {

    // Request location updates using static settings
    // Disabled location updates as it consumes more battery, and location is fetched on interval basis & hero, shipment status
    startLocationUpdates();
    Log.i(TAG, DateFormat.getDateTimeInstance().format(new Date()) + ": Connected");
  }

  @Override public void onConnectionSuspended(int i) {

    // Turn off the request flag
    mInProgress = false;
    // Display the connection status
    // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    Log.i(TAG, DateFormat.getDateTimeInstance().format(new Date()) + ": Suspended");
    mGoogleApiClient.connect();
  }

  /*
   * Called by Location Services if the attempt to
   * Location Services fails.
   */
  @Override public void onConnectionFailed(ConnectionResult connectionResult) {
    mInProgress = false;

    Toast.makeText(this,
        DateFormat.getDateTimeInstance().format(new Date()) + ": Connection Failed",
        Toast.LENGTH_LONG).show();
    Log.i(TAG, DateFormat.getDateTimeInstance().format(new Date()) + ": Connection Failed");
  }



  private void sendLocation(Location currentLocation) {
    Intent intent = new Intent(Constants.ACTION_BROADCAST_LOCATION);
    intent.putExtra("location", currentLocation);
    sendBroadcast(intent);
  }
}
