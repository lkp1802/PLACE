package com.example.jake.gg_place_test;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
    public static String MYPREFERENCE = "MyPrefs";
    //implements OnConnectionFailedListener*/ {
    private GoogleApiClient mGoogleApiClient;
    private static int PLACE_PICKER_REQUEST = 1;
    private Button search, resultButton;
    private EditText editBox, textBox;
    private MainActivity mainActivity;
    private TextView result_view;

    //SharedPreference fields
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();

        search = (Button) findViewById(R.id.search_button);
        textBox = (EditText) findViewById(R.id.nick_name);
        textBox.setVisibility(View.INVISIBLE);
        editBox = (EditText) findViewById(R.id.result_text);
        mainActivity = this;

        resultButton = (Button) findViewById(R.id.json_result);
        result_view = (TextView) findViewById(R.id.test_result);
        resultButton.setVisibility(View.INVISIBLE);
        result_view.setVisibility(View.INVISIBLE);
    }

    public void browseLocation(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                handleUI(place);
                //String toastMsg = String.format("Place: %s", place.getName());
                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleUI(Place place) {
        final String result = place.getName().toString();
        final Place userPlace = place;
        editBox.setText(result);
        textBox.setVisibility(View.VISIBLE);
        textBox.requestFocus();
        //Button search = (Button) findViewById(R.id.search_button);
        search.setText("Add");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "";
                if (textBox != null)
                    name = textBox.getText().toString();
                FavoriteLocation location = new FavoriteLocation(name, userPlace.getId());
                //SharedPreference stuff
                storeLocation(location);
                String toastMsg = String.format("'%s' added!", name);
                Toast.makeText(mainActivity, toastMsg, Toast.LENGTH_LONG).show();

                //resultButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void storeLocation(FavoriteLocation favLocation) {
        //set up SharedPreference
        if (settings == null) {
            settings = getSharedPreferences(MYPREFERENCE, Context.MODE_PRIVATE);
        }
        editor = settings.edit();

        //convert FavoriteLocation to Json
        Gson gson = new Gson();
        String location_json = gson.toJson(favLocation);

        //store json
        String key = "" + favLocation.getNickName();
        editor.putString(key, location_json);
        editor.commit();

        String ID = favLocation.getLocationID();
        //Retrive stored FavortiteLocation object
        location_json = settings.getString(key, "");
        final FavoriteLocation locationID = gson.fromJson(location_json, FavoriteLocation.class);

        //if ((locationID.getLocationID().equals(ID))) {
            //Reconstruct Place oject from its placeID
            locationID.idToPlace(mGoogleApiClient);
            result_view.setText("Sucessfully retrieve: " + locationID.getNickName());
            result_view.setVisibility(View.VISIBLE);
        //}
    }
}
    // TODO: Please implement GoogleApiClient.OnConnectionFailedListener to
    // handle connection failures.
