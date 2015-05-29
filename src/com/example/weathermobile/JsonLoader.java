package com.example.weathermobile;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class JsonLoader extends AsyncTaskLoader<String> {

	private String nameCountry;

	public static String NAME_COUNTRY = "name";

	public JsonLoader(Context context, String nameCountry) {
		super(context);
		this.nameCountry = nameCountry;
	}

	@Override
	public String loadInBackground() {

		return new HttpClient().getWeatherData("Kherson");
	}

}
