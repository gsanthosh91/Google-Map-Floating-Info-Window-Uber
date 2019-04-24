package com.gsanthosh91.decoderoutekey;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

public class DecodeRoute {
    private List<LatLng> polyz = new ArrayList<>();
    private GoogleMap googleMap;
    private Context context;
    private Marker sourceMarker, destinationMarker;
    private DecodeAddress sourceAddress, destinationAddress;

    public DecodeRoute(Context context, GoogleMap googleMap, String encodedPolyPoints) {
        this.context = context;
        this.googleMap = googleMap;
        polyz = decodePolyPoints(encodedPolyPoints);
    }

    public void setSourceAddress(DecodeAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public void setDestinationAddress(DecodeAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void start() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyz) {
            builder.include(latLng);
        }
        final LatLngBounds bounds = builder.build();


        final LatLng origin = polyz.get(0);
        final LatLng destination = polyz.get(polyz.size() - 1);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                if (sourceAddress != null) {
                    View marker_view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                    TextView addressSrc = (TextView) marker_view.findViewById(R.id.addressTxt);
                    TextView etaTxt = (TextView) marker_view.findViewById(R.id.etaTxt);

                    addressSrc.setText(sourceAddress.getAddress());
                    if(sourceAddress.getEta() != null){
                        etaTxt.setText(sourceAddress.getEta());
                        etaTxt.setVisibility(View.VISIBLE);
                    }else {
                        etaTxt.setVisibility(View.GONE);
                    }

                    MarkerOptions marker_opt_source = new MarkerOptions().position(origin);
                    marker_opt_source.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view))).anchor(0.00f, 0.20f);
                    sourceMarker = googleMap.addMarker(marker_opt_source);
                }

                if (destinationAddress != null) {
                    View marker_view2 = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                    TextView addressDes = (TextView) marker_view2.findViewById(R.id.addressTxt);
                    TextView etaTxt = (TextView) marker_view2.findViewById(R.id.etaTxt);

                    addressDes.setText(destinationAddress.getAddress());
                    if(destinationAddress.getEta() != null){
                        etaTxt.setText(destinationAddress.getEta());
                        etaTxt.setVisibility(View.VISIBLE);
                    }else {
                        etaTxt.setVisibility(View.GONE);
                    }

                    MarkerOptions marker_opt_des = new MarkerOptions().position(destination);
                    marker_opt_des.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view2))).anchor(0.00f, 0.20f);
                    destinationMarker = googleMap.addMarker(marker_opt_des);
                }


                googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.dot)).position(origin));
                googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.squre)).position(destination));
                MapAnimator.getInstance().animateRoute(googleMap, polyz, context);

                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (sourceMarker != null) {
                    Point PickupPoint = googleMap.getProjection().toScreenLocation(origin);
                    sourceMarker.setAnchor(PickupPoint.x < dpToPx(context, 200) ? 0.00f : 1.00f, PickupPoint.y < dpToPx(context, 100) ? 0.20f : 1.20f);
                }
                if (destinationMarker != null) {
                    Point PickupPoint = googleMap.getProjection().toScreenLocation(destination);
                    destinationMarker.setAnchor(PickupPoint.x < dpToPx(context, 200) ? 0.00f : 1.00f, PickupPoint.y < dpToPx(context, 100) ? 0.20f : 1.20f);
                }
            }
        });
    }


    private ArrayList<LatLng> decodePolyPoints(String encodedPath) {
        int len = encodedPath.length();

        final ArrayList<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private int dpToPx(Context context, float dpValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dpValue * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
