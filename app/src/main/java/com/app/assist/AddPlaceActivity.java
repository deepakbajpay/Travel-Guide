package com.app.assist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;


public class AddPlaceActivity extends Activity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    EditText latlngEt, nameEt, addressEt, vicinityEt, phoneEt, websiteEt;
    Button btnAddPlace;
    AVLoadingIndicatorView avl;
    AppCompatSpinner categorySp;
//    CustomMapView mapView;
ScrollView mainScrollView;

    String[] categories = new String[]{"ATMs", "Banks", "Bars", "Book Stores", "Doctor", "Education", "Electronics", "Fitness", "Petrol Pumps", "Groceries", "Hospitals", "Hotels", "Insurance", "Movies", "Parks", "Pharmacy", "Police", "Post Office", "Restaurants", "Salons", "Shopping", "Temples","PG"};
    private double mLatitude, mLongitude, placeLat, placeLng;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        Intent intent = getIntent();
        if (intent != null) {
            mLatitude = intent.getDoubleExtra("lat", 0.0);
            mLongitude = intent.getDoubleExtra("lng", 0.0);
        }

        latlngEt = (EditText) findViewById(R.id.add_place_latlng_et);
        nameEt = (EditText) findViewById(R.id.add_place_name_et);
        addressEt = (EditText) findViewById(R.id.add_place_address_et);
        vicinityEt = (EditText) findViewById(R.id.add_place_vicinity_et);
        phoneEt = (EditText) findViewById(R.id.add_place_phone_et);
        websiteEt = (EditText) findViewById(R.id.add_place_website_et);
        btnAddPlace = (Button) findViewById(R.id.activity_add_place_button);
        btnAddPlace.setOnClickListener(this);
        avl = (AVLoadingIndicatorView) findViewById(R.id.avl_indicator_add_place);

        categorySp = (AppCompatSpinner) findViewById(R.id.add_category_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySp.setAdapter(adapter);
        categorySp.getDropDownHorizontalOffset();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mainScrollView = (ScrollView) findViewById(R.id.main_scrollview);

        ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        String name, address, vicinity, phone, website;
        name = nameEt.getText().toString().trim();
        address = addressEt.getText().toString().trim();
        vicinity = vicinityEt.getText().toString().trim();
        phone = phoneEt.getText().toString().trim();
        website = websiteEt.getText().toString().trim();
        String[] latlng = latlngEt.getText().toString().trim().split(",");
        if (latlng.length < 2)
            Utilities.showAlert(this, "Select Location", "Please tap and hold the location of place on the map", "OK", "Cancel");
        else if (TextUtils.isEmpty(name))
            Utilities.showAlert(this, "Empty Field", "Please enter the name of the place", "OK", "Cancel");
        else if (TextUtils.isEmpty(address))
            Utilities.showAlert(this, "Empty Field", "Please enter the address of the place", "OK", "Cancel");
        else if (TextUtils.isEmpty(vicinity))
            Utilities.showAlert(this, "Empty Field", "Please enter the vicinity of the place", "OK", "Cancel");
        else {

            showAvl();
            AddPlaceItem placeItem = new AddPlaceItem();

            placeItem.setLat(latlng[0]);
            placeItem.setLng(latlng[1]);
            placeItem.setName(name);
            placeItem.setAddress(address);
            placeItem.setVicinity(vicinity);
            placeItem.setPhone(phone);
            placeItem.setWebsite(website);
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child(Constant.DB_CHILD_PLACES).child((String) categorySp.getSelectedItem()).push().setValue(placeItem)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    Toast.makeText(AddPlaceActivity.this, "Thanks for the help", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(AddPlaceActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                    hideAvll();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideAvll();
                    Toast.makeText(AddPlaceActivity.this, "Failed to add place", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(AddPlaceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2000);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 15.0f));
        googleMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        placeLat = latLng.latitude;
        placeLng = latLng.longitude;
        latlngEt.setText(placeLat+","+placeLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mGoogleMap.clear();
        mGoogleMap.addMarker(markerOptions);
    }

    private void showAvl(){
        avl.smoothToShow();
        btnAddPlace.setVisibility(View.GONE);
    }

    private void hideAvll(){
        avl.smoothToHide();
        btnAddPlace.setVisibility(View.VISIBLE);
    }

}
