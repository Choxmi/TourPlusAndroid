package com.addah.tourplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by choxmi on 6/22/17.
 */

public class RouteListActivity extends AppCompatActivity implements AsyncResponse {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader,listDataChild;
    Intent intent;
    Map<String,ArrayList<LocationDetails>> routeList;
    LocationDetails startLoc;
    TextView itemTxt,optimTxt;
    int process = 0;
    List<Double> distances;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        intent = getIntent();
        expListView = (ExpandableListView) findViewById(R.id.expList);
        itemTxt = (TextView)findViewById(R.id.itemTxt);
        optimTxt = (TextView)findViewById(R.id.optimTxt);
        startLoc = (LocationDetails) intent.getSerializableExtra("start");
        distances = new ArrayList<>();
        try {
            prepareListData();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        routeList.get(""+groupPosition).get(0).getLoc_name(), Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(RouteListActivity.this,PlanningActivity.class);
                intent.putExtra("start",startLoc);
                intent.putExtra("selected",routeList.get(""+groupPosition));
                startActivity(intent);
                return false;
            }
        });
    }

    private void prepareListData() throws MalformedURLException {
        listDataHeader = new ArrayList<String>();
        listDataChild = new ArrayList<String>();
        routeList = new HashMap<>();

        ArrayList<LocationDetails> ld = (ArrayList<LocationDetails>)intent.getSerializableExtra("selected");
        for(int i = 0;i<ld.size();i++){
            listDataHeader.add("Route "+(i+1));
            Collections.shuffle(ld);
            String child = "";
            ArrayList<LocationDetails> temp = new ArrayList<>();
            for(int j = 0;j<ld.size();j++) {
                child += ld.get(j).getLoc_name()+" -> ";
                temp.add(ld.get(j));
            }
            String waypoints = "";
            for (int x = 0; x < (ld.size()); x++) {
                if (i == 0) {
                    waypoints = "via:" + temp.get(x).getLat() + "%2C" + temp.get(x).getLng();
                } else {
                    waypoints = waypoints + "|via:" + temp.get(x).getLat() + "%2C" + temp.get(x).getLng();
                }
                LatLng sl = new LatLng(temp.get(x).getLat(), temp.get(x).getLng());
            }
            listDataChild.add(child);
            routeList.put(""+i,temp);
            CostConnector connector = new CostConnector("https://maps.googleapis.com/maps/api/directions/xml?origin="+startLoc.getLat() + "%2C" + startLoc.getLng()+"&destination=" + temp.get(ld.size()-1).getLat() + "%2C" + temp.get(ld.size()-1).getLng() + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw");
            connector.delegate = RouteListActivity.this;
            connector.execute();
        }
    }

    @Override
    public void processFinish(String response) {
        String dist = response.substring(0, response.indexOf(' '));
        Log.e("Dist",dist);
        double distance = Double.parseDouble(dist);
        distances.add(distance);
        process++;
        itemTxt.setText(itemTxt.getText()+"\n"+"Route "+process+" : "+response);
        optimTxt.setText("Shortest distance : "+Collections.min(distances)+" | Cost : Rs. "+(25*Collections.min(distances)));
    }
}
