package com.kickwe.port.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import java.net.HttpURLConnection;
import android.net.Uri;
import java.net.URL;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // constructor
    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url) {

        // Making HTTP request
		
		
        try {
            // defaultHttpClient
			URL uri= new URL(url);
            
            
			HttpURLConnection urlConnection = (HttpURLConnection)uri.openConnection();
			
            
            
            is = urlConnection.getInputStream();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
														   is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
}
