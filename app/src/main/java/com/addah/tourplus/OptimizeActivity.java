package com.addah.tourplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by choxmi on 6/24/17.
 */

public class OptimizeActivity extends AppCompatActivity implements AsyncResponse{

    ArrayList<LocationDetails> ld;
    Spinner spinner;
    int arrrayPosi,timeAvail;
    Button proceedBtn,analyzeBtn;
    EditText timeLimit;
    OptimizeConnector connector;
    ArrayList<Integer> timeGaps;
    List<String> items;
    ArrayAdapter<String> adapter;
    ProgressDialog pd;
    ListView rec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimize);
        proceedBtn = (Button)findViewById(R.id.optimusBtn);
        analyzeBtn = (Button)findViewById(R.id.anaBtn);
        timeLimit = (EditText)findViewById(R.id.timeSpnd);
        final Intent intent = getIntent();
        //Get selected path from the planning activity
        ld = (ArrayList<LocationDetails>)intent.getSerializableExtra("path");
        spinner = (Spinner)findViewById(R.id.currLoc);
        items = new ArrayList<>();
        timeGaps = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,items);
        rec = (ListView) findViewById(R.id.suggestionList);
        rec.setAdapter(adapter);
        //List down the locations of the path
        List<String> list = new ArrayList<>();
        for(LocationDetails lds : ld){
            list.add(lds.getLoc_name());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        //Get the selected location
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrrayPosi = position;
                Log.e("posi",""+arrrayPosi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        //Request the distances between every location which are located after the user selected location. ProcessFinish method retrieve and add the result to a list.
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = (arrrayPosi+1);i<ld.size();i++){
                    try {
                        connector = new OptimizeConnector("https://maps.googleapis.com/maps/api/directions/xml?origin=" + ld.get(i-1).getLat() + "%2C" + ld.get(i-1).getLng() + "&destination=" + ld.get(i).getLat() + "%2C" + ld.get(i).getLng() + "&key=AIzaSyCuPQ_L8oFl3tTP7GvdAKTbWe1Cqeu4GXw");
                        connector.delegate = OptimizeActivity.this;
                        connector.execute();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        //Analyze possible suggestions can make.
        analyzeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();
                timeAvail = Integer.parseInt(timeLimit.getText().toString())*60;
                int timeDirect=0,fullTime = 0;
                //Add waiting time of each location to the resultset gained from Google map API call.
                for(int i=0;i<timeGaps.size();i++){
                    timeDirect += timeGaps.get(i);
                    fullTime += timeDirect+ld.get(arrrayPosi+i).getWaiting_time();
                }
                Log.e("direct",""+timeDirect);
                Log.e("time avail",""+timeAvail);
                //Check the time require to complete full journey and time to go directly.
                items.add("Time to Complete journey as planned : "+(fullTime/60)+" min");
                items.add("Time to go Directly to destination : "+(timeDirect/60)+" min");
                adapter.notifyDataSetChanged();
                //Based on the Available time, output the best suggestion.
                if(timeAvail>=fullTime){
                    Toast.makeText(OptimizeActivity.this,"Still have time",Toast.LENGTH_SHORT).show();
                }else if(timeAvail>=timeDirect){
                    Toast.makeText(OptimizeActivity.this,"Don't stop. Go directly",Toast.LENGTH_SHORT).show();
                }else{
                    int timeDir=0;
                    suggest: {
                        for(int i=0;i<timeGaps.size();i++){
                            timeDir += timeGaps.get(i);
                            if(timeDir>timeAvail){
                                Toast.makeText(OptimizeActivity.this,"You have to stay @ "+ld.get(arrrayPosi+i).getLoc_name(),Toast.LENGTH_SHORT).show();
                                items.add("You have to stay @ "+ld.get(arrrayPosi+i).getLoc_name());
                                adapter.notifyDataSetChanged();
                                break suggest;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void processFinish(String response) {
        int time = Integer.parseInt(response);
        timeGaps.add(time);
        Log.e("Res",response);
        if(timeGaps.size()==(ld.size()-arrrayPosi)){
            Toast.makeText(OptimizeActivity.this,"Retrieving complete",Toast.LENGTH_LONG).show();
        }
    }
}
