package com.example.hyk.geoweather;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements GeoWeatherDelegate, LocationListener {

    private GeoWeather geoWeather;
    private boolean getService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            getService = true;
            locationServiceInitial();
        } else {
            Toast.makeText(this, "Please turn on location service!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

    }

    private LocationManager lms;
//    private String bestProvider = LocationManager.GPS_PROVIDER;
    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        bestProvider = lms.getBestProvider(criteria, true);
//        Location location = lms.getLastKnownLocation(bestProvider);

        Location location = null;


        if ( lms.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d("Main","NETWORK");
            location = lms.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else if (lms.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Log.d("Main","GPS");
            location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        this.getLocation(location);
    }

    private void getLocation(Location location) {
        Log.d("Main","getLocation");

        if(location != null) {
            Log.d("Main", "lat = " + location.getLatitude() + "lng = " + location.getLongitude());
            float lat = (float)location.getLatitude();
            float lng = (float)location.getLongitude();

            geoWeather = new GeoWeather();
            geoWeather.delegate = this;
            geoWeather.getAddrWithLatLng(lat, lng);
        }
        else {
            Toast.makeText(this, "Can't get location", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void geoWeatherDidGetCity(String city) {
        geoWeather.getYahooWoeidWithCity(city);
    }

    @Override
    public void geoWeatherDidGetWoeid(String woeid) {
        Log.d("Main","woeid = " + woeid);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Main","onLocationChanged");
        this.getLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Main","onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Main","onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Main","onProviderDisabled");
    }
}
