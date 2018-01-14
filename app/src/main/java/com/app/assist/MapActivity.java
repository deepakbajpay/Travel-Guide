package com.app.assist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapActivity extends Activity implements OnMapReadyCallback, LocationListener {

    private static final int LOCATION_REQUEST_CODE = 912;

    HashMap<String, String> mMarkerPlaceLink = new HashMap();
    HashMap<String, String> placehashmap;
    private GoogleMap mGoogleMap;
    private double mLatitude, mLongitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        placehashmap = (HashMap<String, String>) getIntent().getSerializableExtra(Constant.PLACE_HASHMAP);
        mLatitude = getIntent().getDoubleExtra(Constant.LATITUDE, 0.0);
        mLongitude = getIntent().getDoubleExtra(Constant.LONGITUDE, 0.0);

        com.google.android.gms.maps.MapFragment mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        HashMap<String, String> hmPlace = placehashmap;
        String name = (String) hmPlace.get("place_name");
        String vicinity = (String) hmPlace.get("vicinity");
        Double lat = Double.parseDouble((String) hmPlace.get("lat"));
        Double lng = Double.parseDouble((String) hmPlace.get("lng"));
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.title(new StringBuilder(String.valueOf(name)).append(" : ").append(vicinity).toString());
        mMarkerPlaceLink.put(mGoogleMap.addMarker(markerOptions).getId(), (String) hmPlace.get("reference"));
        mGoogleMap.addMarker(markerOptions);
        System.out.println("PlaceListActivity.onMapReady " + mGoogleMap);
        setUpMap(lat, lng);
    }

    public void setUpMap(Double lat, Double lng) {
        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            this.mGoogleMap.setMyLocationEnabled(true);
            this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12.0f));
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 600000, 0.0f, this);
        this.mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker arg0) {
                Intent intent = new Intent(MapActivity.this, PlaceDetailsActivity.class);
                intent.putExtra("reference", mMarkerPlaceLink.get(arg0.getId()));
                intent.putExtra(Constant.PLACE_HASHMAP,placehashmap);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    public void onLocationChanged(Location location) {
        this.mLatitude = location.getLatitude();
        this.mLongitude = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
