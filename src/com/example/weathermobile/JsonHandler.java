package com.example.weathermobile;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class JsonHandler {

	public Location jsCoord(JSONObject jObj) throws JSONException {
		Location location = new Location("wethaer");
		JSONObject coordObj = getObject("coord", jObj);
		location.setLatitude(getFloat("lat", coordObj));
		location.setLongitude(getFloat("lon", coordObj));

		return location;
	}

	public static JSONObject getObject(String tagName, JSONObject jObj)
			throws JSONException {
		JSONObject subObj = jObj.getJSONObject(tagName);
		return subObj;
	}

	public static float getFloat(String tagName, JSONObject jObj)
			throws JSONException {
		return (float) jObj.getDouble(tagName);
	}

	public static String getString(String tagName, JSONObject jObj)
			throws JSONException {
		return jObj.getString(tagName);
	}

	public static int getInt(String tagName, JSONObject jObj)
			throws JSONException {
		return jObj.getInt(tagName);
	}

}
