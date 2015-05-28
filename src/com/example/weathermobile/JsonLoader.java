package com.example.weathermobile;

import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;

public class JsonLoader extends Loader<String> {

	private String nameCountry;
	private GetJsonTask getGetJsonTask;

	public JsonLoader(Context context, String nameCountry) {
		super(context);
		this.nameCountry = nameCountry;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

	}

	@Override
	protected void onStopLoading() {
		super.onStopLoading();

	}

	@Override
	protected void onForceLoad() {
		super.onForceLoad();
		if (getGetJsonTask != null);
		getGetJsonTask.cancel(true);
		new GetJsonTask().execute();

	}

	class GetJsonTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			return new HttpClient().getWeatherData(nameCountry);

		}

	}
}
