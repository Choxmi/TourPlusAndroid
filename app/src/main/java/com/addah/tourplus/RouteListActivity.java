package com.addah.tourplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by choxmi on 6/22/17.
 */
//This class is for the suggestion generation part
public class RouteListActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader,listDataChild;
    Intent intent;
    Map<String,ArrayList<LocationDetails>> routeList;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        //Get data from Place selector activity.
        intent = getIntent();
        expListView = (ExpandableListView) findViewById(R.id.expList);
        //Refer preparedListData method
        prepareListData();
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
                intent.putExtra("selected",routeList.get(""+groupPosition));
                startActivity(intent);
                return false;
            }
        });
    }

    //This method generate suggestions about the route.
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new ArrayList<String>();
        routeList = new HashMap<>();
        //Get the selected location list from the intent
        ArrayList<LocationDetails> ld = (ArrayList<LocationDetails>)intent.getSerializableExtra("selected");
        //Shuffle the locations and generate all possible routes
        for(int i = 0;i<ld.size();i++){
            listDataHeader.add("Route "+(i+1));
            //Randomize the selection by shuffling the location list
            Collections.shuffle(ld);
            String child = "";
            ArrayList<LocationDetails> temp = new ArrayList<>();
            for(int j = 0;j<ld.size();j++) {
                child += ld.get(j).getLoc_name()+" -> ";
                temp.add(ld.get(j));
            }
            //Add shuffled locations list to the list. One result for one possible route.
            listDataChild.add(child);
            routeList.put(""+i,temp);
            //After adding one result, it will display in expListView. Likewise it'll happen to every possible route.
        }
    }
}
