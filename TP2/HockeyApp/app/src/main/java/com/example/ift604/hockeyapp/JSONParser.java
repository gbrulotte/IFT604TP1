package com.example.ift604.hockeyapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by veip1702 on 2015-10-01.
 */
public class JSONParser {

    static JSONArray jArray = null;

    public JSONParser(){

    }

    public JSONArray getJSONArrayFromString(String data) {
        try {
            jArray = new JSONArray(data);
        } catch(JSONException e) {
            Log.e("JSONParser", "Error parsing data " + e.toString());
        }

        return jArray;
    }
}
