package ru.edu.mikashkinov.datafromsensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sm;
    Sensor accelerometer;
    TextView acceleration, proximity, light;
    List<Sensor> deviceSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        deviceSensors = sm.getSensorList(Sensor.TYPE_ALL);
        for(Sensor s: deviceSensors) {
            Log.e("sensor list",s.getName());
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //accelerometer = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        //sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView)findViewById(R.id.acceleration);
        proximity = (TextView)findViewById(R.id.proximity);
        light = (TextView)findViewById(R.id.light);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleration.setText(" X: " + event.values[0] +
                                "\nY: " + event.values[1] +
                                "\nZ: " + event.values[2]);
        } else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity.setText("" + event.values[0]);
        } else if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light.setText("" + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
