package com.addah.tourplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

/**
 * Created by choxmi on 5/24/17.
 */

public class NewPlacesActivity extends AppCompatActivity {
    int REQUEST_PLACE_PICKER = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            Log.e("Place",name.toString());
            Intent waiting = new Intent(this, WaitingTimeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("Latlng",place.getLatLng());
            waiting.putExtra("bundle",bundle);
            waiting.putExtra("Name",place.getName());
            startActivity(waiting);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
