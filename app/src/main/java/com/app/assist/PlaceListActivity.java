package com.app.assist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, PlaceAdapterClickCallback {
    private static final int LOCATION_REQUEST_CODE = 912;
    private static List<HashMap<String, String>> list;
    RecyclerView recyclerView;
    PlaceAdapterItem adapterItem;
    GoogleMap mGoogleMap;
    double mLatitude = 0.0d;
    double mLongitude = 0.0d;
    HashMap<String, String> mMarkerPlaceLink = new HashMap();
    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;
    String types = "";
    String selectType = "";
    ProgressDialog mProgressDialog;
    private StringBuilder stringBuilder;

    public /* bridge */ /* synthetic */ View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(view, str, context, attributeSet);
    }

    public /* bridge */ /* synthetic */ View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return super.onCreateView(str, context, attributeSet);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_list_activity);

        Intent i = getIntent();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Looking places for you! Give me a minute");
        mProgressDialog.setTitle("Please Wait");
        mProgressDialog.show();
        this.types = i.getStringExtra("type");
        this.selectType = i.getStringExtra("selectedType");
        System.out.println("PlaceListActivity.onCreate type = " + types);
        this.mLatitude = i.getDoubleExtra("lat", 0.0d);
        this.mLongitude = i.getDoubleExtra("log", 0.0d);

        list = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.rv_places);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaceListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapterItem = new PlaceAdapterItem(PlaceListActivity.this, list, types,selectType, this);
        recyclerView.setAdapter(adapterItem);

        ((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled("gps");
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != 0) {
            GooglePlayServicesUtil.getErrorDialog(status, this, 10).show();
            return;
        }


        NetworkInfo netInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
        String mtype = this.types.toLowerCase();
        stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location=" + this.mLatitude + "," + this.mLongitude);
        stringBuilder.append("&radius=20000");
        stringBuilder.append("&types=" + mtype);
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=AIzaSyBKfm8PvKtLkf1SxRPqkxhnZiybichvzTs");
        System.out.println("PlaceListActivity.onCreate stringBuilder" + stringBuilder.toString());
        PlaceListActivity placeListActivity = this;
        getPlacesFromFirebase();


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(selectType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:

                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


    @Override
    public void onPlaceAdapterClick(int viewId, int position) {
        switch (viewId) {
            case R.id.iv_verify:

                break;
            default:
               Intent intent = new Intent(this,MapActivity.class);
                intent.putExtra(Constant.LATITUDE,mLatitude);
                intent.putExtra(Constant.LONGITUDE,mLongitude);
                intent.putExtra(Constant.PLACE_HASHMAP,list.get(position));
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;

        }

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(strUrl).openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String str = "";
            while (true) {
                str = br.readLine();
                if (str == null) {
                    break;
                }
                sb.append(str);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception downldng url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void onLocationChanged(Location location) {
        this.mLatitude = location.getLatitude();
        this.mLongitude = location.getLongitude();
        this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(this.mLatitude, this.mLongitude)));
        this.mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
    }

    public void onProviderDisabled(String arg0) {
    }

    public void onProviderEnabled(String arg0) {
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    public boolean onNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        new PlacesTask().execute(stringBuilder.toString());
        System.out.println("PlaceListActivity.onMapReady " + mGoogleMap);
        setUpMap();
    }

    public void setUpMap() {
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
            this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.mLatitude, this.mLongitude), 15.0f));
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 600000, 0.0f, this);
        this.mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker arg0) {
                Intent intent = new Intent(PlaceListActivity.this, PlaceDetailsActivity.class);
                intent.putExtra("reference", PlaceListActivity.this.mMarkerPlaceLink.get(arg0.getId()));
                PlaceListActivity.this.startActivity(intent);
                PlaceListActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    private void getPlacesFromFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constant.DB_CHILD_PLACES).child(selectType);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("PlaceListActivity.onDataChange " + dataSnapshot);
                PlaceListActivity.list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    AddPlaceItem item = ds.getValue(AddPlaceItem.class);
                    if (Constant.EMAIL_ADMIN.equalsIgnoreCase(Utilities.getString(PlaceListActivity.this,Utilities.EMAIL))) {
                        HashMap<String, String> placeMap = new HashMap<String, String>();
                        placeMap.put("place_name", item.getName());
                        placeMap.put("vicinity", item.getVicinity());
                        placeMap.put("lat", item.getLat());
                        placeMap.put("lng", item.getLng());
                        placeMap.put("verified", item.getVerified());
                        placeMap.put("id", ds.getKey());
                        placeMap.put("phone", item.getPhone());
                        placeMap.put("website", item.getWebsite());
                        placeMap.put("address", item.getAddress());
                        list.add(placeMap);
                    }else{
                        if ("true".equalsIgnoreCase(item.getVerified())) {
                            HashMap<String, String> placeMap = new HashMap<String, String>();
                            placeMap.put("place_name", item.getName());
                            placeMap.put("vicinity", item.getVicinity());
                            placeMap.put("lat", item.getLat());
                            placeMap.put("lng", item.getLng());
                            placeMap.put("verified", item.getVerified());
                            placeMap.put("id", ds.getKey());
                            placeMap.put("phone", item.getPhone());
                            placeMap.put("website", item.getWebsite());
                            placeMap.put("address", item.getAddress());
                            list.add(placeMap);
                        }
                    }
                }
                new PlacesTask().execute(stringBuilder.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        JSONObject jObject;

        private ParserTask() {
        }

        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            try {
                this.jObject = new JSONObject(jsonData[0]);
                System.out.println("ParserTask.doInBackground " + jObject.toString());
                places = placeJsonParser.parse(this.jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        protected void onPostExecute(List<HashMap<String, String>> list) {

            PlaceListActivity.list.addAll(list);
            adapterItem.notifyDataSetChanged();
            /*PlaceListActivity.this.mGoogleMap.clear();
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> hmPlace = (HashMap) list.get(i);
                String name = (String) hmPlace.get("place_name");
                String vicinity = (String) hmPlace.get("vicinity");
                markerOptions.position(new LatLng(Double.parseDouble((String) hmPlace.get("lat")), Double.parseDouble((String) hmPlace.get("lng"))));
                markerOptions.title(new StringBuilder(String.valueOf(name)).append(" : ").append(vicinity).toString());
                PlaceListActivity.this.mMarkerPlaceLink.put(PlaceListActivity.this.mGoogleMap.addMarker(markerOptions).getId(), (String) hmPlace.get("reference"));
            }*/

            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        }
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {
        String data;

        private PlacesTask() {
            this.data = null;
        }

        protected String doInBackground(String... url) {
            try {
                this.data = PlaceListActivity.this.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return this.data;
        }

        protected void onPostExecute(String result) {
            new ParserTask().execute(new String[]{result});
        }
    }


}
