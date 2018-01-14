package com.app.assist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceJSONParser {
    public List<HashMap<String, String>> parse(JSONObject jObject) {
        JSONArray jPlaces = null;
        try {
            jPlaces = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jPlaces);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList();
        for (int i = 0; i < placesCount; i++) {
            try {
                placesList.add(getPlace((JSONObject) jPlaces.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject jPlace) {
        HashMap<String, String> place = new HashMap();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String rating = "-NA-";
        try {
            if (!jPlace.isNull("name")) {
                placeName = jPlace.getString("name");
                vicinity = jPlace.getString("vicinity");

                try {
                    rating = jPlace.getString("rating");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = jPlace.getString("reference");
            place.put("place_name", placeName);
            place.put("vicinity", vicinity);
            place.put("lat", latitude);
            place.put("lng", longitude);
            place.put("reference", reference);
            place.put("rating",rating);
            place.put("verified","true");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}
