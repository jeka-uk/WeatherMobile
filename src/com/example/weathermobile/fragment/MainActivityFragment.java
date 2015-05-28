package com.example.weathermobile.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.weathermobile.Constants;
import com.example.weathermobile.HttpClient;
import com.example.weathermobile.JsonHandler;
import com.example.weathermobile.R;

import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityFragment extends Fragment implements OnKeyListener, LoaderCallbacks<String> {

	private static final String LOG_TAG = "myLogs";

	private TextView temp_TextView, pressure_TextView, wind_TextView,
			humidity_TextView;
	private EditText countryName_TextView;
	private ImageView weatherIcon;
	private String nameCountry;
	private DecimalFormat dec = new DecimalFormat("0.00");
	private JsonHandler mJsonHandler = new JsonHandler();

	private class WeatherTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			return new HttpClient().getWeatherData(nameCountry);
		}

		@Override
		protected void onPostExecute(String strJson) {
			super.onPostExecute(strJson);

			try {

				jsonParsing(new JSONObject(strJson));

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		temp_TextView = (TextView) view.findViewById(R.id.temp_TextView);
		pressure_TextView = (TextView) view
				.findViewById(R.id.pressure_TextView);
		wind_TextView = (TextView) view.findViewById(R.id.wind_TextView);
		weatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
		countryName_TextView = (EditText) view
				.findViewById(R.id.countryName_TextView);
		countryName_TextView.setOnKeyListener(this);
		humidity_TextView = (TextView) view
				.findViewById(R.id.humidity_TextView);

		return view;
	}

	private void jsonParsing(JSONObject jObj) throws JSONException {

		JSONObject coordObj = mJsonHandler.getObject("coord", jObj);
		// sys
		JSONObject sysObj = mJsonHandler.getObject("sys", jObj);
		// weather
		JSONArray jArr = jObj.getJSONArray("weather");
		JSONObject JSONWeather = jArr.getJSONObject(0);
		// main
		JSONObject mainObj = mJsonHandler.getObject("main", jObj);
		// Wind
		JSONObject wObj = mJsonHandler.getObject("wind", jObj);
		// Clouds
		JSONObject cObj = mJsonHandler.getObject("clouds", jObj);
		// (getInt("all", cObj))

		temp_TextView.setText(String.valueOf(dec.format(mJsonHandler.getFloat(
				"temp", mainObj) - 273.15)) + " " + getString(R.string.grad));

		pressure_TextView.setText(String.valueOf(mJsonHandler.getInt(
				"pressure", mainObj)) + " " + getString(R.string.pres));
		wind_TextView.setText(String.valueOf(mJsonHandler.getFloat("speed",
				wObj)) + " " + getString(R.string.win));
		humidity_TextView.setText(String.valueOf(mJsonHandler.getInt(
				"humidity", mainObj)) + " " + getString(R.string.hum));
		Picasso.with(getActivity())
				.load("http://openweathermap.org/img/w/"
						+ mJsonHandler.getString("icon", JSONWeather) + ".png")
				.into(weatherIcon);

	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

			switch (view.getId()) {
			case R.id.countryName_TextView:

				isNetworkConnected();

				nameCountry = countryName_TextView.getText().toString();

				InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				break;

			}
			return true;

		}
		return false;
	}

	private void isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {

			showMessage("No internet connection");

		} else {

			new WeatherTask().execute();

		}
	}

	public void showMessage(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}

	@Override
	public Loader<String> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<String> arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
		// TODO Auto-generated method stub
		
	}

}
