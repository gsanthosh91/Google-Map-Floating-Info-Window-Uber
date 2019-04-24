package com.gsanthosh91.floatinginfowindow;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.gsanthosh91.decoderoutekey.DecodeAddress;
import com.gsanthosh91.decoderoutekey.DecodeRoute;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        DecodeRoute decodeRoute = new DecodeRoute(this, googleMap, "{punAgqyhNIgAWAeE[iBKLh@VdA\\z@t@rALr@SrDdC@BHj@lDPvB?t@Ax@DRBf@Df@f@lFdHcBp@QnDy@jBi@bCiA|E_CrDaBhAu@BIWQ_BmCQF_C~@");
        decodeRoute.setSourceAddress(new DecodeAddress("Prestige palladium bayan", "4 mins"));
        decodeRoute.setDestinationAddress(new DecodeAddress("Anna nagar west", null));
        decodeRoute.start();
    }

}
