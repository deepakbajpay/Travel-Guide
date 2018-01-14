package com.app.assist;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlaceDetailsJSONParser {
    public HashMap<String, String> parse(JSONObject jObject) {
        JSONObject jPlaceDetails = null;
        try {
            jPlaceDetails = jObject.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaceDetails(jPlaceDetails);
    }

    private HashMap<String, String> getPlaceDetails(JSONObject jPlaceDetails) {
        HashMap<String, String> hPlaceDetails = new HashMap();
        String name = "-NA-";
        String icon = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String formatted_address = "-NA-";
        String formatted_phone = "-NA-";
        String website = "-NA-";
        String rating = "-NA-";
        String international_phone_number = "-NA-";
        String url = "-NA-";
        try {
            if (!jPlaceDetails.isNull("name")) {
                name = jPlaceDetails.getString("name");
            }
            if (!jPlaceDetails.isNull("icon")) {
                icon = jPlaceDetails.getString("icon");
            }
            if (!jPlaceDetails.isNull("vicinity")) {
                vicinity = jPlaceDetails.getString("vicinity");
            }
            if (!jPlaceDetails.isNull("formatted_address")) {
                formatted_address = jPlaceDetails.getString("formatted_address");
            }
            if (!jPlaceDetails.isNull("formatted_phone_number")) {
                formatted_phone = jPlaceDetails.getString("formatted_phone_number");
            }
            if (!jPlaceDetails.isNull("website")) {
                website = jPlaceDetails.getString("website");
            }
            if (!jPlaceDetails.isNull("rating")) {
                rating = jPlaceDetails.getString("rating");
            }
            if (!jPlaceDetails.isNull("international_phone_number")) {
                international_phone_number = jPlaceDetails.getString("international_phone_number");
            }
            if (!jPlaceDetails.isNull("url")) {
                url = jPlaceDetails.getString("url");
            }
            latitude = jPlaceDetails.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlaceDetails.getJSONObject("geometry").getJSONObject("location").getString("lng");
            hPlaceDetails.put("name", name);
            hPlaceDetails.put("icon", icon);
            hPlaceDetails.put("vicinity", vicinity);
            hPlaceDetails.put("lat", latitude);
            hPlaceDetails.put("lng", longitude);
            hPlaceDetails.put("formatted_address", formatted_address);
            hPlaceDetails.put("formatted_phone", formatted_phone);
            hPlaceDetails.put("website", website);
            hPlaceDetails.put("rating", rating);
            hPlaceDetails.put("international_phone_number", international_phone_number);
            hPlaceDetails.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hPlaceDetails;
    }
}
