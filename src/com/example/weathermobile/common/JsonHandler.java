package com.example.weathermobile.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class JsonHandler {

	public double getDoubleSubObj(JSONObject jObj, String namesubObj, String key)throws JSONException {

		double result = jObj.getJSONObject(namesubObj).getDouble(key);

		return result;
	}
	
	public String getStringSubObj(JSONObject jObj, String namesubObj, String key)throws JSONException {

		String result = jObj.getJSONObject(namesubObj).getString(key);

		return result;
	}

	public String getStringArrey(JSONObject jObj, String nameArrey, String key)throws JSONException {

		String result = jObj.getJSONArray(nameArrey).getJSONObject(0).getString(key);

		return result;
	}

	public double getDoubleObj(JSONObject jObj, String key)	throws JSONException {

		double result = jObj.getDouble(key);

		return result;
	}
	
	public String getStringleObj(JSONObject jObj, String key)	throws JSONException {

		String result = jObj.getString(key);

		return result;
	}

}
