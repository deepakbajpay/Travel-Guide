package com.app.assist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainPage extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, View.OnClickListener {
    public static final int MULTIPLE_PERMISSIONS = 10;
    public Integer[] thumbid = new Integer[]{Integer.valueOf(R.drawable.atm), Integer.valueOf(R.drawable.bank), Integer.valueOf(R.drawable.bar), Integer.valueOf(R.drawable.bookstore), Integer.valueOf(R.drawable.doctor), Integer.valueOf(R.drawable.education), Integer.valueOf(R.drawable.elec), Integer.valueOf(R.drawable.fitness), Integer.valueOf(R.drawable.petrol), Integer.valueOf(R.drawable.grocery), Integer.valueOf(R.drawable.hospital), Integer.valueOf(R.drawable.hotel), Integer.valueOf(R.drawable.insurance), Integer.valueOf(R.drawable.movies), Integer.valueOf(R.drawable.park), Integer.valueOf(R.drawable.pharmacy), Integer.valueOf(R.drawable.police), Integer.valueOf(R.drawable.post), Integer.valueOf(R.drawable.restaurant), Integer.valueOf(R.drawable.beauty), Integer.valueOf(R.drawable.shopping), Integer.valueOf(R.drawable.temple),R.drawable.ic_pg};
    protected int _splashTime = 500;
    protected LocationRequest locationRequest;
    protected GoogleApiClient mGoogleApiClient;
    String[] permissions = new String[]{"android.permission.INTERNET", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_NETWORK_STATE", "android.permission.CHANGE_NETWORK_STATE"};
    int REQUEST_CHECK_SETTINGS = 100;
    ArrayList<BeanDemo> array = new ArrayList();
    BeanDemo bd;
    GPSTracker gps;
    GridView gv;
    Intent intent;
    String st;
    String[] values = new String[]{"ATMs", "Banks", "Bars", "Book Stores", "Doctor", "Education", "Electronics", "Fitness", "Petrol Pumps", "Groceries", "Hospitals", "Hotels", "Insurance", "Movies", "Parks", "Pharmacy", "Police", "Post Office", "Restaurants", "Salons", "Shopping", "Temples", "PG"};
    FloatingActionButton fab;

    ProgressBar progressBar;
    private double latitude, longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
//        toolbar.setTitle("Assist");
//        setSupportActionBar(toolbar);

        call_permissions();
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        fab = (FloatingActionButton) findViewById(R.id.fab_home_fragment);
        fab.setOnClickListener(this);

        this.mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        this.mGoogleApiClient.connect();
        this.locationRequest = LocationRequest.create();
        this.locationRequest.setPriority(100);
        this.locationRequest.setInterval(30000);
        this.locationRequest.setFastestInterval(5000);

        this.gv = (GridView) findViewById(R.id.gv1);
        for (int i = 0; i < this.values.length; i++) {
            this.bd = new BeanDemo();
            this.bd.setName(this.values[i]);
            this.bd.setImage(this.thumbid[i]);
            this.array.add(this.bd);
        }
        this.gv.setAdapter(new AdapterDemo(getApplicationContext(), R.layout.main_page, this.array));
        this.gv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MainPage.this.gps = new GPSTracker(MainPage.this);
                if (MainPage.this.gps.canGetLocation()) {
                    latitude = MainPage.this.gps.getLatitude();
                    longitude = MainPage.this.gps.getLongitude();
                    Toast.makeText(MainPage.this.getApplicationContext(), "Please wait we are looking for " + MainPage.this.values[position], Toast.LENGTH_SHORT).show();
                    MainPage.this.intent = new Intent(MainPage.this, PlaceListActivity.class);
                    intent.putExtra("selectedType", values[position]);

                    if ("ATMs".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "atm";
                    }
                    if ("Banks".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "bank";
                    }
                    if ("Bars".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "bar";
                    }
                    if ("Salons".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "beauty_salon";
                    }
                    if ("Book Stores".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "book_store";
                    }
                    if ("Doctor".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "doctor";
                    }
                    if ("Education".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "school";
                    }
                    if ("Electronics".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "electronics_store";
                    }
                    if ("Fitness".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "gym";
                    }
                    if ("Petrol Pumps".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "gas_station";
                    }
                    if ("Groceries".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "grocery_or_supermarket";
                    }
                    if ("Hospitals".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "hospital";
                    }
                    if ("Hotels".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "lodging";
                    }
                    if ("Insurance".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "insurance_agency";
                    }
                    if ("Movies".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "movie_theater";
                    }
                    if ("Parks".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "park";
                    }
                    if ("Pharmacy".equals(MainPage.this.values[position])) {
                        MainPage.this.st = "pharmacy";

                    }
                    if (MainPage.this.values[position] == "Police") {
                        MainPage.this.st = "police";
                    }
                    if (MainPage.this.values[position] == "Post Office") {
                        MainPage.this.st = "post_office";
                    }
                    if (MainPage.this.values[position] == "Restaurants") {
                        MainPage.this.st = "restaurant";
                    }
                    if (MainPage.this.values[position] == "Shopping") {
                        MainPage.this.st = "shopping_mall";
                    }
                    if (MainPage.this.values[position] == "Temples") {
                        MainPage.this.st = "hindu_temple";
                    }
                    if (MainPage.this.values[position] == "PG") {
                        MainPage.this.st = "pg";
                    }
                    MainPage.this.intent.putExtra("type", MainPage.this.st);
                    MainPage.this.intent.putExtra("lat", latitude);
                    MainPage.this.intent.putExtra("log", longitude);
                    MainPage.this.startActivity(MainPage.this.intent);
                    MainPage.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    return;
                }
                MainPage.this.gps.showSettingsAlert();
            }
        });
    }

    public void onBackPressed() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                MainPage.this.finish();
                System.exit(0);
            }
        }, 200);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings /*2131296317*/:
                startActivity(new Intent(getApplicationContext(), About.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                return true;
            case R.id.logout:
                Utilities.clearPreferences(this);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onConnected(Bundle arg0) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(this.locationRequest);
        builder.setAlwaysShow(true);
        LocationServices.SettingsApi.checkLocationSettings(this.mGoogleApiClient, builder.build()).setResultCallback(this);
    }

    public void onConnectionSuspended(int arg0) {
    }

    public void onConnectionFailed(ConnectionResult arg0) {
    }

    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        try {
            status.startResolutionForResult(this, this.REQUEST_CHECK_SETTINGS);
            return;
        } catch (SendIntentException e) {
            return;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != this.REQUEST_CHECK_SETTINGS) {
            return;
        }
        if (resultCode == -1) {
            Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void call_permissions() {
        List<String> listPermissionsNeeded = new ArrayList();
        for (String p : this.permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), p) != 0) {
                listPermissionsNeeded.add(p);
            }
        }
        if (listPermissionsNeeded.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    progressBar.setVisibility(View.GONE);
//                    MainPage.this.finish();
//                    MainPage.this.startActivity(new Intent(MainPage.this, MainPage.class));
//                    MainPage.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                    MainPage.this.finish();
                }
            }, (long) this._splashTime);
        } else {
            ActivityCompat.requestPermissions(this, (String[]) listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS /*10*/:
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Warning!!!").setMessage("This application requires permission to work properly.").setCancelable(false).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainPage.this.finish();
                        }
                    });
                    builder.create().show();
                    return;
                }
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.GONE);
//                        MainPage.this.finish();
//                        MainPage.this.startActivity(new Intent(MainPage.this, MainPage.class));
//                        MainPage.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                        MainPage.this.finish();
                    }
                }, (long) this._splashTime);
                return;
            default:
                return;
        }
    }

    @Override
    public void onClick(View view) {
        MainPage.this.gps = new GPSTracker(MainPage.this);
        if (MainPage.this.gps.canGetLocation()) {
            latitude = MainPage.this.gps.getLatitude();
            longitude = MainPage.this.gps.getLongitude();
            switch (view.getId()) {
                case R.id.fab_home_fragment:
                    Intent intent = new Intent(this, AddPlaceActivity.class);
                    intent.putExtra("lat", latitude);
                    intent.putExtra("lng", longitude);
                    startActivity(intent);
                    break;
            }
        } else
            Toast.makeText(this, "Can't get your location", Toast.LENGTH_SHORT).show();
    }
}
