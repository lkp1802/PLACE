package com.example.jake.gg_place_test;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by Jake on 4/28/16.
 */
public class FavoriteLocation {
    private String nickName;
    private String placeID;
    private Double[] latlon;
    public FavoriteLocation(String nickName, String location){
        this.nickName = nickName;
        this.placeID = location;
        this.latlon = new Double[2];
    }

    //Getter
    public String getNickName() {
        return nickName;
    }

    public String getLocationID(){
        return placeID;
    }

    public void idToPlace(GoogleApiClient mGoogleApiClient){
        final Place[] myPlace = new Place[1];

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                    latlon[0] = places.get(0).getLatLng().latitude;
                    latlon[1] = places.get(0).getLatLng().longitude;
                }
            }
        });
    }
}
