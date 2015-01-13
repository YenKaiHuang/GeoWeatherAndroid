package com.example.hyk.geoweather;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by hyk on 15/1/7.
 */
public class GeoWeather {

    protected final String GoogleGeoToCityURL = "http://maps.googleapis.com/maps/api/geocode/xml?language=EN&latlng=";
    protected final String YahooGeoPlaceURL = "http://query.yahooapis.com/v1/public/yql?q=select+*+from+geo.places+where+text+='";

    public GeoWeatherDelegate delegate;

    public String woeid;
    public String city;

    public void getAddrWithLatLng(float lat, float lng){
        final String addrUrl = GoogleGeoToCityURL + String.valueOf(lat) + "," + String.valueOf(lng);
        Log.d("Geo", "addrUrl = " + addrUrl);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle geo = client.get(addrUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                String s = new String(bytes);
//                Log.d("Geo", "responce = " + s);
                InputStream is = new ByteArrayInputStream(bytes);

                HashMap address = null;
                String key = null;
                String value = null;

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser pullParser = factory.newPullParser();
                    pullParser.setInput(is, "utf-8");
                    int eventType = pullParser.getEventType();
                    Log.d("Geo", "eventType = " + eventType);
                    while(eventType != XmlPullParser.END_DOCUMENT)
                    {
                        if (eventType == XmlPullParser.START_TAG) {
                            String name = pullParser.getName();
                            Log.d("Geo", "START_TAG name = " + name);
                            if (name.equals("address_component")) {
                                address = new HashMap<String, String>();
                            }else if (name.equals("long_name") | name.equals("type")) {
                                key = name;
                            }
                        } else if (eventType == XmlPullParser.END_TAG) {
                            String name = pullParser.getName();
                            Log.d("Geo", "END_TAG name = " + name);
                            if (name.equals("address_component")) {

                            }else if (name.equals("long_name")) {
                                address.put(key, value);
                                key = null;
                                value = null;
                            }else if(name.equals("type")){
                                if (value.equals("administrative_area_level_4")) {
                                    city = address.get("long_name").toString();
                                    Log.d("Geo", "city = " + city);
                                    delegate.geoWeatherDidGetCity(city);
                                    break;
                                }
                            }
                        } else if (eventType==XmlPullParser.TEXT) {
                            value = pullParser.getText();
                            Log.d("Geo", "TEXT value = " + value);

                        }
                        eventType = pullParser.next();
                    }
                } catch (XmlPullParserException e){
                    Log.d("Geo", "error = "+e.toString());

                } catch (IOException e){
                    Log.d("Geo", "error = "+e.toString());

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void getYahooWoeidWithCity(String city){
        final String cityUrl = YahooGeoPlaceURL + city + "'";
        Log.d("Geo", "cityUrl = " + cityUrl);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle geo = client.get(cityUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                InputStream is = new ByteArrayInputStream(bytes);

                String value = null;

                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser pullParser = factory.newPullParser();
                    pullParser.setInput(is, "utf-8");
                    int eventType = pullParser.getEventType();
                    Log.d("Geo", "eventType = " + eventType);
                    while(eventType != XmlPullParser.END_DOCUMENT)
                    {
                        if (eventType == XmlPullParser.START_TAG) {
                            String name = pullParser.getName();
                            Log.d("Geo", "START_TAG name = " + name);
                            if (name.equals("woeid")) {

                            }
                        } else if (eventType == XmlPullParser.END_TAG) {
                            String name = pullParser.getName();
                            Log.d("Geo", "END_TAG name = " + name);
                            if (name.equals("woeid")) {
                                woeid = value;
                                Log.d("Geo", "woeid = " + woeid);
                                delegate.geoWeatherDidGetWoeid(woeid);
                                break;
                            }
                        } else if (eventType==XmlPullParser.TEXT) {
                            value = pullParser.getText();
                            Log.d("Geo", "TEXT value = " + value);

                        }
                        eventType = pullParser.next();
                    }
                } catch (XmlPullParserException e){
                    Log.d("Geo", "error = "+e.toString());

                } catch (IOException e){
                    Log.d("Geo", "error = "+e.toString());

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }



}
