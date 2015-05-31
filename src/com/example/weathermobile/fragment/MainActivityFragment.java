package com.example.weathermobile.fragment;

import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.weathermobile.Constants;
import com.example.weathermobile.HttpClient;
import com.example.weathermobile.JsonHandler;
import com.example.weathermobile.JsonLoader;
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
import android.support.v4.content.Loader.ForceLoadContentObserver;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityFragment extends Fragment implements LoaderCallbacks<String> {

	private static final String LOG_TAG = "myLogs";

	private TextView mTempTextView, mPressureTextView, mWindTextView, mHumidityTextView;
	private EditText mCountryNameTextView;
	private ImageView mWeatherIcon;
	private String mNameCountry;
	private Button mRun_startButton;
	private DecimalFormat dec = new DecimalFormat("0.00");
	private JsonHandler mJsonHandler = new JsonHandler();
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    		
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		mTempTextView = (TextView) view.findViewById(R.id.temp_TextView);
		mPressureTextView = (TextView) view.findViewById(R.id.pressure_TextView);
		mWindTextView = (TextView) view.findViewById(R.id.wind_TextView);
		mWeatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
		mCountryNameTextView = (EditText) view.findViewById(R.id.countryName_TextView);
		mHumidityTextView = (TextView) view.findViewById(R.id.humidity_TextView);
		
		mRun_startButton = (Button) view.findViewById(R.id.run_startButton);
		
		mRun_startButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                mNameCountry = mCountryNameTextView.getText().toString();
                isNetworkConnected();
               // Log.v(LOG_TAG, mNameCountry);   
                
            }
        });
		
		return view;
	}

	private void setData(JSONObject jObj) throws JSONException {

		mTempTextView.setText(String.valueOf(dec.format(mJsonHandler.getDoubleSubObj(jObj, "main", "temp") - 273.15))+ " "+ getString(R.string.grad));
		mWindTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj,"wind", "speed")) + " " + getString(R.string.win));
		mPressureTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "pressure")) + " " + getString(R.string.pres));
		mHumidityTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "humidity")) + " " + getString(R.string.hum));
		Picasso.with(getActivity()).load(Constants.ICON_URL	+ mJsonHandler.getStringArrey(jObj, "weather", "icon")+ ".png").into(mWeatherIcon);
	}

	private void isNetworkConnected() {
		
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		
		if (ni == null) {
			showMessage("No internet connection");

		} else {
			
		    getLoaderManager().initLoader(Constants.LOADER_TIME_ID, null, this).forceLoad();
		    
		}
	}

	public void showMessage(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
	}

	@Override
	public Loader<String> onCreateLoader(int arg0, Bundle arg1) {
	    
	    Log.v(LOG_TAG, mNameCountry);
			
		JsonLoader loder = new JsonLoader(getActivity(), mNameCountry);
		return loder;
	}

	@Override
	
	public void onLoadFinished(Loader<String> arg0, String strJson) {
		
		try {
			setData(new JSONObject(strJson));
		} catch (JSONException e) {

			e.printStackTrace();
		}
		
		getLoaderManager().restartLoader(Constants.LOADER_TIME_ID, null, this).forceLoad();				
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
				
	}
	
}
