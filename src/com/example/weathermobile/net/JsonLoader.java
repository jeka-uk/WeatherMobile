package com.example.weathermobile.net;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class JsonLoader extends AsyncTaskLoader<String> {

	private String mNameCountry;
	
	private static final String LOG_TAG = "myLogs";

	public JsonLoader(Context context, String mNameCountry) {
		super(context);
		this.mNameCountry = mNameCountry;
	}

	@Override
	public String loadInBackground() {

		return new HttpClient().getWeatherData(mNameCountry);
	}

}
