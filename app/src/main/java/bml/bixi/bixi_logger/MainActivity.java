package bml.bixi.bixi_logger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager mSensorManager ;
    boolean isRunning;
    final String TAG = "SensorLog";
    FileWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isRunning = false;

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        LineChart Accchart = (LineChart) findViewById(R.id.Accelrometerchart);
        LineChart Gychart = (LineChart) findViewById(R.id.Gyrochart);
        LineChart Mchart = (LineChart) findViewById(R.id.Magnetochart);


        final Button buttonStart = findViewById(R.id.button_start);
        final Button buttonStop = findViewById(R.id.button_stop);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);

                Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                Sensor mMagnometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                Sensor mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
                Sensor mLinerAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                Sensor mRotVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                Sensor mMotion = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);


                mSensorManager.registerListener(MainActivity.this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(MainActivity.this, mMagnometer, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(MainActivity.this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(MainActivity.this, mLinerAccel, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(MainActivity.this, mRotVector, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(MainActivity.this, mMotion, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(MainActivity.this, mMotion, SensorManager.SENSOR_DELAY_FASTEST);
                mSensorManager.registerListener(MainActivity.this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

                Log.d(TAG, "Writing to " + getStorageDir());
                try {
                    writer = new FileWriter(new File(getStorageDir(), "sensors_" + System.currentTimeMillis() + ".csv"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isRunning = true;
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);

                isRunning = false;
                mSensorManager.flush(MainActivity.this);
                mSensorManager.unregisterListener(MainActivity.this);
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        /*mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMagnometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mLinerAccel, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mRotVector, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mMotion, SensorManager.SENSOR_DELAY_FASTEST);*/
    }

    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(isRunning) {
            Log.d(TAG, "Running " );
            try {
                long timeInMillis = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
                Log.d(TAG, "Running "+timeInMillis );
                switch(event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        writer.write(String.format("%d; ACCELEROMETER; %f;\t%f;\t%f\n", timeInMillis, event.values[0], event.values[1], event.values[2]));
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        writer.write(String.format("%d; GYROSCOPE; %f;\t%f;\t%f\n", timeInMillis, event.values[0], event.values[1], event.values[2]));
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        writer.write(String.format("%d; MAGNETIC; %f;\t%f;\t%f\n", timeInMillis, event.values[0], event.values[1], event.values[2]));
                        break;
                    case Sensor.TYPE_GRAVITY:
                        writer.write(String.format("%d; GRAVITY; %f;\t%f;\t%f\n", timeInMillis, event.values[0], event.values[1], event.values[2]));
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        writer.write(String.format("%d; LINEAR_ACCELERATION; %f;\t%f;\t%f\n", timeInMillis, event.values[0], event.values[1], event.values[2]));
                        Log.d(TAG, "Linear acc " );
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        writer.write(String.format("%d; ROTATION; %f;\t%f;\t%f;\t%f\n", timeInMillis, event.values[0], event.values[1], event.values[2], event.values[3]));
                        Log.d(TAG, "ROT vect " );
                        break;
                    case Sensor.TYPE_SIGNIFICANT_MOTION:
                        writer.write(String.format("%d; MOTION; %f;\t%f;\t%f;\t%f\n", timeInMillis, event.values[0], event.values[1], event.values[2], event.values[3]));
                        Log.d(TAG, "Motion " );
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String getStorageDir() {
        return this.getExternalFilesDir(null).getAbsolutePath();
        //  return "/storage/emulated/0/Android/data/com.iam360.sensorlog/";
    }
}
