package ru.edu.mikashkinov.datafromsensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.telephony.SignalStrength;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Map<String, String> currentState = new HashMap<>();
    File data = new File("/sdcard/dataset.csv");

    SensorManager sm;
    TelephonyManager telephonyManager;
    LocationManager locationManager;

    TextView acceleration, proximity, light, signal, gps_acc, gps_long, gps_lat, gps_alt, gps_speed, gps_time;
    List<Sensor> deviceSensors;

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            gps_acc.setText(  "Accuracy: "  + location.getAccuracy());
            gps_long.setText( "Longitude: " + location.getLongitude());
            gps_lat.setText(  "Latitude: "  + location.getLatitude());
            gps_alt.setText(  "Altitude: "  + location.getAltitude());
            gps_speed.setText("Speed: "     + location.getSpeed());
            gps_time.setText( "Time: "      + location.getTime());

            currentState.put("gps_acc", "" + location.getAccuracy());
            currentState.put("gps_long",  ""  + location.getLongitude());
            currentState.put("gps_lat",   ""  + location.getLatitude());
            currentState.put("gps_alt",   ""  + location.getAltitude());
            currentState.put("gps_speed", ""  + location.getSpeed());
            currentState.put("gps_time",  ""  + location.getTime());
        }

        public void onStatusChanged(String s, int i, Bundle b) {
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }

    }

    PhoneStateListener pslistener = new PhoneStateListener() {
        public int signalStrengthValue;


        @Override
        public void onSignalStrengthChanged(int asu) {
            signal.setText("" + asu);
            currentState.put("signal", "" + asu);
            super.onSignalStrengthChanged(asu);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            if (signalStrength.isGsm())
                signalStrengthValue = signalStrength.getGsmSignalStrength();
            else {
                int strength = -1;
                if (signalStrength.getEvdoDbm() < 0)
                    strength = signalStrength.getEvdoDbm();
                else if (signalStrength.getCdmaDbm() < 0)
                    strength = signalStrength.getCdmaDbm();
                if (strength < 0) {
                    signalStrengthValue = Math.round((strength + 113f) / 2f);
                }

            }
            signal.setText("" + signalStrengthValue);
            currentState.put("signal", "" + signalStrengthValue);
            super.onSignalStrengthsChanged(signalStrength);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        deviceSensors = sm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : deviceSensors) {
            Log.e("sensor list", s.getName());
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);

        }

        if ((data.length() == 0) || !data.exists()) {
            FileWriter writer;
            try {
                writer = new FileWriter(data, true);
                writer.write("Class," + "Signal_Strength," + "Light," + "Proximity," + "AccelerationX," + "accelerationY," + "accelerationZ," + "GPS_Accuracy," + "GPS_Longitude," + "GPS_Latitude," + "GPS_Altitude," + "GPS_Speed," + "GPS_Time"  + "\n");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(pslistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new MyLocationListener());

        acceleration = (TextView)findViewById(R.id.acceleration);
        proximity = (TextView)findViewById(R.id.proximity);
        light = (TextView)findViewById(R.id.light);
        signal = (TextView)findViewById(R.id.signal);
        gps_acc = (TextView)findViewById(R.id.gps_acc);
        gps_long = (TextView)findViewById(R.id.gps_long);
        gps_lat = (TextView)findViewById(R.id.gps_lat);
        gps_alt = (TextView)findViewById(R.id.gps_alt);
        gps_speed = (TextView)findViewById(R.id.gps_speed);
        gps_time = (TextView)findViewById(R.id.gps_time);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleration.setText( "X: " + event.values[0] +
                                "\nY: " + event.values[1] +
                                "\nZ: " + event.values[2]);
            currentState.put("accelerationX", "" + event.values[0]);
            currentState.put("accelerationY", "" + event.values[1]);
            currentState.put("accelerationZ", "" + event.values[2]);
            Log.e("TEST!!!!", currentState.get("accelerationX"));

        } else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity.setText("" + event.values[0]);
            currentState.put("proximity", "" + event.values[0]);
        } else if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light.setText("" + event.values[0]);
            currentState.put("light", "" + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void indoor_onClick(View v) throws IOException {
        FileWriter dataset = new FileWriter(data, true);
        dataset.write("1.0," + currentState.get("signal") + ","
                             + currentState.get("light") + ","
                             + currentState.get("proximity") + ","
                             + currentState.get("accelerationX") + ","
                             + currentState.get("accelerationY") + ","
                             + currentState.get("accelerationZ")  + ","
                             + currentState.get("gps_acc") + ","
                             + currentState.get("gps_long") + ","
                             + currentState.get("gps_lat") + ","
                             + currentState.get("gps_alt") + ","
                             + currentState.get("gps_speed") + ","
                             + currentState.get("gps_time") + "\n");
        dataset.flush();
        Toast.makeText(MainActivity.this,
                "Data dumped as INDOOR",
                Toast.LENGTH_SHORT).show();
      }

    public void outdoor_onClick(View v) throws IOException {
        FileWriter dataset = new FileWriter(data, true);
        dataset.write("0.0," + currentState.get("signal") + ","
                             + currentState.get("light") + ","
                             + currentState.get("proximity") + ","
                             + currentState.get("accelerationX") + ","
                             + currentState.get("accelerationY") + ","
                             + currentState.get("accelerationZ")  + ","
                             + currentState.get("gps_acc") + ","
                             + currentState.get("gps_long") + ","
                             + currentState.get("gps_lat") + ","
                             + currentState.get("gps_alt") + ","
                             + currentState.get("gps_speed") + ","
                             + currentState.get("gps_time") + "\n");
        dataset.flush();
        Toast.makeText(MainActivity.this,
                "Data dumped as OUTDOOR",
                Toast.LENGTH_SHORT).show();
    }
}


