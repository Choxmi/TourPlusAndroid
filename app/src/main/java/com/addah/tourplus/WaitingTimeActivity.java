package com.addah.tourplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;

/**
 * Created by choxmi on 6/10/17.
 */

public class WaitingTimeActivity extends AppCompatActivity implements AsyncResponse{

    TextView nameTxt,coordinatesTxt;
    Button addBtn;
    GenericConnector connector;
    EditText waiting,description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_time);
        nameTxt = (TextView) findViewById(R.id.place_name_txt);
        coordinatesTxt = (TextView) findViewById(R.id.latlng_txt);
        Intent rec = getIntent();
        nameTxt.setText(rec.getStringExtra("Name"));
        final Bundle bundle = rec.getParcelableExtra("bundle");
        coordinatesTxt.setText(bundle.getParcelable("Latlng").toString());
        waiting = (EditText)findViewById(R.id.waiting_time_edit);
        description = (EditText)findViewById(R.id.descTxt);
        addBtn = (Button)findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LatLng latLng = (bundle.getParcelable("Latlng"));
                    String name = nameTxt.getText().toString().replaceAll(" ","%20");
                    String desc = description.getText().toString().replaceAll(" ","%20");
                    connector = new GenericConnector("https://harvester.000webhostapp.com/Tour.php?status=push&name="+name+"&lat="+latLng.latitude+"&lng="+latLng.longitude+"&wait="+waiting.getText().toString()+"&desc="+desc);
                    connector.delegate = WaitingTimeActivity.this;
                    connector.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void processFinish(String response) {
        Log.e("res",response);
        if(response.equals("done")){
            Toast.makeText(WaitingTimeActivity.this,"Place added successfully",Toast.LENGTH_LONG).show();
        }
    }
}
