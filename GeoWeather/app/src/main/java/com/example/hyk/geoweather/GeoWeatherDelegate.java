package com.example.hyk.geoweather;

/**
 * Created by hyk on 15/1/9.
 */
public interface GeoWeatherDelegate {
    public void geoWeatherDidGetCity(String city);
    public void geoWeatherDidGetWoeid(String woeid);
}
