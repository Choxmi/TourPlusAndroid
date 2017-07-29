package com.addah.tourplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by choxmi on 6/22/17.
 */

public class PlanningActivity extends AppCompatActivity implements AsyncResponse{
    ListView rec;
    List<String> items;
    RouteConnector connector;
    ArrayAdapter<String> adapter;
    int totalDu=0;
    int totalWaiting = 0;
    Button optBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planing);
        rec = (ListView) findViewById(R.id.details);
        items = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,items);
        rec.setAdapter(adapter);
        optBtn = (Button)findViewById(R.id.optBtn);
        String waypoints = "";
        final Intent intent = getIntent();
        //Get the selected locations and details about those selected places from the PlaceSelector Activity
        ArrayList<LocationDetails> ld = (ArrayList<LocationDetails>)intent.getSerializableExtra("selected");

        //Go through every location and request distances between each locations destinations and other details about the location from Google maps
        for (int i = 0; i <= ld.size(); i++) {
            try {
                if(i==0) {
                    connector = new RouteConnector("https://maps.googleapis.com/maps/api/directions/xml?origin=colombo,sl&destination=" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng() + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw", "* Colombo to "+ld.get(i).getLoc_name(),"Waiting @ "+ld.get(i).getLoc_name()+" : "+ld.get(i).getWaiting_time()+" mins");
                    Log.e("URL","https://maps.googleapis.com/maps/api/directions/xml?origin=colombo,sl&destination=" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng() + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw");
                    totalWaiting += ld.get(i).getWaiting_time();
                }else if(i==(ld.size())){
                    connector = new RouteConnector("https://maps.googleapis.com/maps/api/directions/xml?origin=" + ld.get(i-1).getLat() + "%2C" + ld.get(i-1).getLng() + "&destination=jaffna,sl&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw","* "+ld.get(i-1).getLoc_name()+" to Jaffna","Destination Arrived");
                }else{
                    connector = new RouteConnector("https://maps.googleapis.com/maps/api/directions/xml?origin=" + ld.get(i-1).getLat() + "%2C" + ld.get(i-1).getLng() + "&destination=" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng() + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw","* "+ld.get(i-1).getLoc_name()+" to "+ld.get(i).getLoc_name(),"Waiting @ "+ld.get(i).getLoc_name()+" : "+ld.get(i).getWaiting_time()+" mins");
                    totalWaiting += ld.get(i).getWaiting_time();
                }
                connector.delegate = PlanningActivity.this;
                connector.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        //Get Latitude and Logitude values of each and every location and create a waypoint list which helps to define the path
        for (int i = 0; i < ld.size(); i++) {
            if (i == 0) {
                waypoints = "via:" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng();
            } else {
                waypoints = waypoints + "|via:" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng();
            }
            LatLng sl = new LatLng(ld.get(i).getLat(), ld.get(i).getLng());
        }

        Log.e("Waypoint", waypoints);

        //Make a Google map request to get the destination and time. And the waiting time of every location will be added in the RouterConnector class.
        try {
            connector = new RouteConnector("https://maps.googleapis.com/maps/api/directions/xml?origin=colombo,sl&destination=jaffna,sl&waypoints=" + waypoints + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw","* Total Duration : ",""+totalWaiting+" mins");
            connector.delegate = PlanningActivity.this;
            connector.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        optBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PlanningActivity.this,OptimizeActivity.class);
                intent1.putExtra("path",intent.getSerializableExtra("selected"));
                startActivity(intent1);
            }
        });
    }

    //Here we can retrieve all the responces from the API calls we made. List details will be added from here and that will be the final output.
    @Override
    public void processFinish(String response) {
        if(response.contains("-")){
            String[] res;
            res = response.split("-");
            response = res[0];
            totalDu = Integer.parseInt(res[1])+(totalWaiting*60);
        }
        items.add(response);
        if(totalDu!=0){
            int hr = totalDu/3600;
            int min = (totalDu%3600)/60;
            items.add("Total Trip : "+hr+"hr "+min+"min");
        }
        adapter.notifyDataSetChanged();
    }
}
