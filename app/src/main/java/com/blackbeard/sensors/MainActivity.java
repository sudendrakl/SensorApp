package com.blackbeard.sensors;

import android.hardware.Sensor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.data.env.AmbientTemperatureData;
import com.ubhave.sensormanager.data.pull.AccelerometerData;
import com.ubhave.sensormanager.data.pull.GyroscopeData;
import com.ubhave.sensormanager.data.pull.LocationData;
import com.ubhave.sensormanager.data.push.BatteryData;
import com.ubhave.sensormanager.sensors.pull.LocationSensor;
import com.ubhave.sensormanager.sensors.SensorUtils;

public class MainActivity extends AppCompatActivity {

  private ESSensorManager sm;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton refreshButton = (FloatingActionButton) findViewById(R.id.fab);
    refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
      }
    });

    try {
      sm = ESSensorManager.getSensorManager(this);
    } catch (ESException e) {
      e.printStackTrace();
    }


  }


  void getData() throws ESException {
    AmbientTemperatureData temp = (AmbientTemperatureData)sm.getDataFromSensor(SensorUtils.SENSOR_TYPE_AMBIENT_TEMPERATURE);
    LocationData loc = (LocationData) sm.getDataFromSensor(SensorUtils.SENSOR_TYPE_LOCATION);
    AccelerometerData acc = (AccelerometerData) sm.getDataFromSensor(SensorUtils.SENSOR_TYPE_ACCELEROMETER);
    GyroscopeData gyro = (GyroscopeData) sm.getDataFromSensor(Sensor.TYPE_GYROSCOPE);
    BatteryData batt = (BatteryData) sm.getDataFromSensor(Sensor.TYPE_B)

  }
}
