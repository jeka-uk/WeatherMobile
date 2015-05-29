package com.example.weathermobile;

import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

public class JsonLoader extends AsyncTaskLoader<String> {

	private String mNameCountry;
	public final static String NAME_COUNTRY = "name_country";

	public JsonLoader(Context context, Bundle args) {
		super(context);
		if (args != null);
		mNameCountry = args.getString(NAME_COUNTRY);
	}

	@Override
	public String loadInBackground() {

		return new HttpClient().getWeatherData(mNameCountry);
	}
}
