package com.addah.tourplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by choxmi on 6/13/17.
 */

public class PlaceSelectActivity extends AppCompatActivity implements AsyncResponse {
    GenericConnector connector;
    ProgressDialog pd;
    AutoCompleteTextView textView;
    Button add_btn,continue_btn;
    ListView listView;
    ArrayAdapter<String> adapter,adapter_mul;
    TextView descTxt;
    final ArrayList<LocationDetails> locations = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_select);
        textView = (AutoCompleteTextView)findViewById(R.id.search_txt);
        add_btn = (Button)findViewById(R.id.add_btn);
        continue_btn = (Button)findViewById(R.id.cont_btn);
        descTxt = (TextView) findViewById(R.id.descTxt);
        pd = new ProgressDialog(PlaceSelectActivity.this,ProgressDialog.STYLE_SPINNER);
        listView = (ListView)findViewById(R.id.place_list);
        //Get all location data from our database using the URL
        try {
            connector = new GenericConnector("https://harvester.000webhostapp.com/Tour.php?status=pull");
            connector.delegate = PlaceSelectActivity.this;
            connector.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        pd.setMessage("Fetching Places");
        pd.show();
    }

    //Get the result from the URL as a JSON
    @Override
    public void processFinish(String response) {
        pd.hide();
        String[] strArr=null;
        //Convert JSON data to an LocationDetails objects
        try {
            JSONArray ja = new JSONArray(response);
            strArr = new String[ja.length()];
            for (int i =0;i<ja.length();i++){
                JSONObject jo = ja.getJSONObject(i);
                strArr[i] = jo.getString("Name");
                LocationDetails ld = new LocationDetails();
                ld.setLoc_name(jo.getString("Name"));
                ld.setLat(jo.getDouble("Lat"));
                ld.setLng(jo.getDouble("Lng"));
                ld.setWaiting_time(jo.getInt("Waiting"));
                ld.setDetails(jo.getString("description"));
                locations.add(ld);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PlaceSelectActivity.this,android.R.layout.simple_dropdown_item_1line,strArr);
        textView.setAdapter(adapter);
        
        final ArrayList<LocationDetails> selected = new ArrayList<>();
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean trigger = false;
                for(int i = 0;i<locations.size();i++){
                    if(locations.get(i).getLoc_name().equals(textView.getText().toString())){
                        selected.add(locations.get(i));
                        trigger = true;
                    }
                }
                if(!trigger){
                    Toast.makeText(PlaceSelectActivity.this,"Sorry! Place Unavailable",Toast.LENGTH_LONG).show();
                }
                textView.setText("");
            }
        });
        
        adapter_mul = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, strArr);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter_mul);
        //Add the selected locatio to selected list and show the description
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationDetails ld = (LocationDetails) locations.get(position);
                Log.e("Det",ld.getDetails());
                if(!ld.getDetails().equals("null")){
                    descTxt.setText(ld.getDetails());
                }else {
                    descTxt.setText("No description Found");
                }
            }
        });

        selected.clear();
        //Proceed to RouteSelector Activity
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {

                    int position = checked.keyAt(i);

                    if (checked.valueAt(i))
                        selected.add(locations.get(position));
                        //selectedItems.add(adapter_mul.getItem(position));
                }
                Intent intent = new Intent(PlaceSelectActivity.this,RouteListActivity.class);
                intent.putExtra("selected",selected);
                startActivity(intent);
            }
        });
    }
}
