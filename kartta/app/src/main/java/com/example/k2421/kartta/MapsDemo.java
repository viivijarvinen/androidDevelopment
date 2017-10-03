package com.example.k2421.kartta;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsDemo extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private JSONArray locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_demo);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FetchDataTask task = new FetchDataTask();
        task.execute("http://student.labranet.jamk.fi/~K2421/otot.json");


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


        class FetchDataTask extends AsyncTask<String, Void, JSONObject> {
            @Override
            protected JSONObject doInBackground(String... urls) {
                HttpURLConnection urlConnection = null;
                JSONObject json = null;
                try {
                    URL url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    json = new JSONObject(stringBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
                return json;
            }

            protected void onPostExecute(JSONObject json) {
                StringBuffer text = new StringBuffer("");
                try {
                    // store highscores
                    locations = json.getJSONArray("locations");
                    for (int i=0;i < locations.length();i++) {
                        JSONObject location = locations.getJSONObject(i);

                        LatLng otto = new LatLng(Double.valueOf(location.getString("lat")),Double.valueOf(location.getString("lon")));
                        mMap.addMarker(new MarkerOptions().position(otto).title(location.getString("address")));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(otto));
                    }
                } catch (JSONException e) {
                    Log.e("JSON", "Error getting data.");
                }

            }
        }
    }



