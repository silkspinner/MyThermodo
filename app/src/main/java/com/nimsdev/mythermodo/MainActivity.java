package com.nimsdev.mythermodo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nimsdev.mythermodo.Thermodo;
import com.nimsdev.mythermodo.ThermodoFactory;
import com.nimsdev.mythermodo.ThermodoListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class MainActivity extends Activity implements ThermodoListener {

    private static Logger sLog = Logger.getLogger(MainActivity.class.getName());
    private Thermodo mThermodo;
    private TextView mTemperatureTextView;
    private TextView mRecipeTextView;

    private float myTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTemperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        mRecipeTextView = (TextView) findViewById(R.id.recipeTextView);
        mRecipeTextView.setText("Here is my finest Beer recipe with alfalfa\nUse lots of Barley and 1/4 hops\n");

        mThermodo = ThermodoFactory.getThermodoInstance(this);
        mThermodo.setThermodoListener(this);
    }

    public void logTemperature(View view) {
        String recipe = mRecipeTextView.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        recipe = recipe +  String.format("%s: %,.2f%n", timeStamp, myTemperature);

        mRecipeTextView.setText(recipe);
    }

    public float celsiousToFahrenheit(float celsius) {
        float fahrenheit = (celsius * (9/5)) + 32;
        return fahrenheit;
    }

    @Override
    public void onStartedMeasuring() {
        Toast.makeText(this, "Started measuring", Toast.LENGTH_SHORT).show();
        sLog.info("Started measuring");
    }

    @Override
    public void onStoppedMeasuring() {
        Toast.makeText(this, "Stopped measuring", Toast.LENGTH_SHORT).show();
        mTemperatureTextView.setText(getString(R.string.thermodo_unplugged));
        sLog.info("Stopped measuring");
    }

    @Override
    public void onTemperatureMeasured(float temperature) {
        mTemperatureTextView.setText(Float.toString(celsiousToFahrenheit(temperature)));
        myTemperature = celsiousToFahrenheit(temperature);
        sLog.fine("Got temperature: " + celsiousToFahrenheit(temperature));
    }

    @Override
    public void onErrorOccurred(int what) {
        Toast.makeText(this, "An error has occurred: " + what, Toast.LENGTH_SHORT).show();
        switch (what) {
            case Thermodo.ERROR_AUDIO_FOCUS_GAIN_FAILED:
                sLog.severe("An error has occurred: Audio Focus Gain Failed");
                mTemperatureTextView.setText(getString(R.string.thermodo_unplugged));
                break;
            case Thermodo.ERROR_AUDIO_RECORD_FAILURE:
                sLog.severe("An error has occurred: Audio Record Failure");
                break;
            case Thermodo.ERROR_SET_MAX_VOLUME_FAILED:
                sLog.warning("An error has occurred: The volume could not be set to maximum");
                break;
            default:
                sLog.severe("An unidentified error has occurred: " + what);
        }
    }


    @Override
    public void onPermissionsMissing() {
        Log.d("DC", "permissions are missing");
    }

    @Override
    public void onThermodoPlugged(Boolean isPlugged) {
        if (isPlugged) {
            mTemperatureTextView.setText(getString(R.string.thermodo_plugged));
        } else {
            mTemperatureTextView.setText(getString(R.string.thermodo_unplugged));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mThermodo.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mThermodo.stop();
    }
}

