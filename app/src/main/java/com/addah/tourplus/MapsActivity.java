package com.addah.tourplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.nearby.messages.Distance;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,AsyncResponse {

    private GoogleMap mMap;
    private Connector connector;
    SharedPreferences sf;
    Map<String,Polyline> plList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sf = getSharedPreferences("Tour",MODE_PRIVATE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String waypoints = "";
        Intent intent = getIntent();
        ArrayList<LocationDetails> ld = (ArrayList<LocationDetails>)intent.getSerializableExtra("selected");

            for (int i = 0; i < ld.size(); i++) {
                if (i == 0) {
                    waypoints = "via:" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng();
                } else {
                    waypoints = waypoints + "|via:" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng();
                }
                LatLng sl = new LatLng(ld.get(i).getLat(), ld.get(i).getLng());
                mMap.addMarker(new MarkerOptions().position(sl).title(ld.get(i).getLoc_name()));
            }

            Log.e("Waypoint", waypoints);
            //https://maps.googleapis.com/maps/api/directions/json?origin=sydney,au&destination=perth,au&waypoints=via:-37.81223%2C144.96254|via:-34.92788%2C138.60008&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw

            try {
                connector = new Connector("https://maps.googleapis.com/maps/api/directions/xml?origin=colombo,sl&destination=jaffna,sl&waypoints=" + waypoints + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw");
                connector.delegate = MapsActivity.this;
                connector.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void processFinish(String response) {
        response = response.replaceAll("\\s+","");
        List<LatLng> cordinates = PolyUtil.decode(response);
        PolylineOptions line = new PolylineOptions();
        line.width(15);
        line.color(Color.BLUE);
        for (LatLng latLng : cordinates) {
            line.add(latLng);
        }
        line.clickable(true);
        Polyline plline = mMap.addPolyline(line);
        //plList.put(plline.getId(),plline);
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                polyline.setColor(Color.RED);
                Log.e("ID",polyline.getId());
//                Log.e("Poly ID",sf.getString("id",""));
//                if(!sf.getString("id","").equals("")){
//                    Polyline pl = plList.get(sf.getString("id",""));
//                    pl.setColor(Color.BLUE);
//                }
//                sf.edit().putString("id",polyline.getId()).commit();
//                Log.e("Poly ID AFTER",sf.getString("id",""));
            }
        });
    }
}
