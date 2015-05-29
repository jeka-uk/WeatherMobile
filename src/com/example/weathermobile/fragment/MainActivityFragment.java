package com.example.weathermobile.fragment;

import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.weathermobile.Constants;
import com.example.weathermobile.HttpClient;
import com.example.weathermobile.JsonHandler;
import com.example.weathermobile.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityFragment extends Fragment implements OnKeyListener, LoaderCallbacks<String> {

	private static final String LOG_TAG = "myLogs";

	private TextView temp_TextView, pressure_TextView, wind_TextView,humidity_TextView;
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

				setData(new JSONObject(strJson));

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
		pressure_TextView = (TextView) view.findViewById(R.id.pressure_TextView);
		wind_TextView = (TextView) view.findViewById(R.id.wind_TextView);
		weatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
		countryName_TextView = (EditText) view.findViewById(R.id.countryName_TextView);
		countryName_TextView.setOnKeyListener(this);
		humidity_TextView = (TextView) view.findViewById(R.id.humidity_TextView);
		
		return view;
	}

	private void setData(JSONObject jObj) throws JSONException {

		temp_TextView.setText(String.valueOf(dec.format(mJsonHandler.getDoubleSubObj(jObj, "main", "temp") - 273.15))+ " "+ getString(R.string.grad));
		wind_TextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj,"wind", "speed")) + " " + getString(R.string.win));
		pressure_TextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "pressure")) + " " + getString(R.string.pres));
		humidity_TextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "humidity")) + " " + getString(R.string.hum));
		Picasso.with(getActivity()).load(Constants.ICON_URL	+ mJsonHandler.getStringArrey(jObj, "weather", "icon")+ ".png").into(weatherIcon);
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			switch (view.getId()) {
			case R.id.countryName_TextView:

				isNetworkConnected();

				nameCountry = countryName_TextView.getText().toString();

				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				
				break;
			}
			return true;
		}
		return false;
	}

	private void isNetworkConnected() {
		
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		
		if (ni == null) {
			showMessage("No internet connection");

		} else {

			new WeatherTask().execute();
			
			//getLoaderManager().initLoader(Constants.LOADER_TIME_ID, null, this).forceLoad();
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
		temp_TextView.setText(arg1);
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
		// TODO Auto-generated method stub
		
	}

}
