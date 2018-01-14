package com.app.assist;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class PlaceDetailsActivity extends AppCompatActivity {
    WebView mWvPlaceDetails;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        this.mWvPlaceDetails = (WebView) findViewById(R.id.wv_place_details);
        this.mWvPlaceDetails.getSettings().setUseWideViewPort(false);
        String reference = getIntent().getStringExtra("reference");
        if (reference != null){
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("reference=" + reference);
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyCMCJ4nVbk_z5GmtBxy9O5J8f-lpRn631k");
        new PlacesTask().execute(new String[]{sb.toString()});
    }else{
            HashMap<String,String> hPlaceDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constant.PLACE_HASHMAP);

            String name = (String) hPlaceDetails.get("place_name");
            String icon = (String) hPlaceDetails.get("icon");
            String vicinity = (String) hPlaceDetails.get("vicinity");
            String lat = (String) hPlaceDetails.get("lat");
            String lng = (String) hPlaceDetails.get("lng");
            String formatted_address = (String) hPlaceDetails.get("address");
            String formatted_phone = (String) hPlaceDetails.get("phone");
            String website = (String) hPlaceDetails.get("website");
            String rating = (String) hPlaceDetails.get("rating");
            String international_phone_number = (String) hPlaceDetails.get("international_phone_number");
            String url = (String) hPlaceDetails.get("url");
            String data = "<html><body><img style='float:left' src=" + icon + " /><h1><center>" + name + "</center></h1>" + "<br style='clear:both' />" + "<hr />" + "<p>Vicinity : " + vicinity + "</p>" + "<p>Location : " + lat + "," + lng + "</p>" + "<p>Address : " + formatted_address + "</p>" + "<p>Phone : " + formatted_phone + "</p>" + "<p>Website : " + website + "</p>" + "<p>Rating : " + rating + "</p>" + "<p>International Phone : " + international_phone_number + "</p>" + "<p>URL : <a href='" + url + "'>" + url + "</p>" + "</body></html>";
            PlaceDetailsActivity.this.mWvPlaceDetails.loadDataWithBaseURL("", data, "text/html", "utf-8", "");

        }

        try {
            getSupportActionBar().setHomeButtonEnabled(true);
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
            Log.d("Exception downlodng url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public boolean onNavigateUp() {
        finish();
        return true;
    }

    private class ParserTask extends AsyncTask<String, Integer, HashMap<String, String>> {
        JSONObject jObject;

        private ParserTask() {
        }

        protected HashMap<String, String> doInBackground(String... jsonData) {
            HashMap<String, String> hPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
            try {
                this.jObject = new JSONObject(jsonData[0]);
                hPlaceDetails = placeDetailsJsonParser.parse(this.jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return hPlaceDetails;
        }

        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {
           updateView(hPlaceDetails);
        }
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {
        String data;

        private PlacesTask() {
            this.data = null;
        }

        protected String doInBackground(String... url) {
            try {
                this.data = PlaceDetailsActivity.this.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return this.data;
        }

        protected void onPostExecute(String result) {
            new ParserTask().execute(new String[]{result});
        }
    }

    private void updateView(HashMap<String,String> hPlaceDetails){
        String name = (String) hPlaceDetails.get("name");
        String icon = (String) hPlaceDetails.get("icon");
        String vicinity = (String) hPlaceDetails.get("vicinity");
        String lat = (String) hPlaceDetails.get("lat");
        String lng = (String) hPlaceDetails.get("lng");
        String formatted_address = (String) hPlaceDetails.get("formatted_address");
        String formatted_phone = (String) hPlaceDetails.get("formatted_phone");
        String website = (String) hPlaceDetails.get("website");
        String rating = (String) hPlaceDetails.get("rating");
        String international_phone_number = (String) hPlaceDetails.get("international_phone_number");
        String url = (String) hPlaceDetails.get("url");
        String data = "<html><body><img style='float:left' src=" + icon + " /><h1><center>" + name + "</center></h1>" + "<br style='clear:both' />" + "<hr />" + "<p>Vicinity : " + vicinity + "</p>" + "<p>Location : " + lat + "," + lng + "</p>" + "<p>Address : " + formatted_address + "</p>" + "<p>Phone : " + formatted_phone + "</p>" + "<p>Website : " + website + "</p>" + "<p>Rating : " + rating + "</p>" + "<p>International Phone : " + international_phone_number + "</p>" + "<p>URL : <a href='" + url + "'>" + url + "</p>" + "</body></html>";
        PlaceDetailsActivity.this.mWvPlaceDetails.loadDataWithBaseURL("", data, "text/html", "utf-8", "");
    }

}
