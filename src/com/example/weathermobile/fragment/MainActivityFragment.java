package com.example.weathermobile.fragment;

import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.weathermobile.Constants;
import com.example.weathermobile.JsonHandler;
import com.example.weathermobile.JsonLoader;
import com.example.weathermobile.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityFragment extends Fragment implements LoaderCallbacks<String> {

	private static final String LOG_TAG = "myLogs";

	private TextView mTempTextView, mPressureTextView, mWindTextView, mHumidityTextView;
	private ImageView mWeatherIcon;
	private String mNameCity;
	private DecimalFormat dec = new DecimalFormat("0.00");
	private JsonHandler mJsonHandler = new JsonHandler();
	private Location mCityLocation = new Location("city location");
	private SearchView searchView;
	private SupportMapFragment mapFragment;
	private GoogleMap mGoogleMap;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		    setHasOptionsMenu(true);
	    		
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		/*mTempTextView = (TextView) view.findViewById(R.id.temp_TextView);
		mPressureTextView = (TextView) view.findViewById(R.id.pressure_TextView);
		mWindTextView = (TextView) view.findViewById(R.id.wind_TextView);
		mWeatherIcon = (ImageView) view.findViewById(R.id.weatherIcon);
		mHumidityTextView = (TextView) view.findViewById(R.id.humidity_TextView);*/
		
				
		return view;
	}

	private void setData(JSONObject jObj) throws JSONException {

		/*mTempTextView.setText(String.valueOf(dec.format(mJsonHandler.getDoubleSubObj(jObj, "main", "temp") - 273.15))+ " "+ getString(R.string.grad));
		mWindTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj,"wind", "speed")) + " " + getString(R.string.win));
		mPressureTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "pressure")) + " " + getString(R.string.pres));
		mHumidityTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "humidity")) + " " + getString(R.string.hum));
		Picasso.with(getActivity()).load(Constants.ICON_URL	+ mJsonHandler.getStringArrey(jObj, "weather", "icon")+ ".png").into(mWeatherIcon);*/
		
		mCityLocation.setLatitude(mJsonHandler.getDoubleSubObj(jObj, "coord", "lat"));
		mCityLocation.setLongitude(mJsonHandler.getDoubleSubObj(jObj, "coord", "lon"));
		
		movingCamera(mCityLocation);
	}

	private void isNetworkConnected() {
		
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNinfo = cm.getActiveNetworkInfo();
		
		if (mNinfo == null) {
			
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
			
		JsonLoader loder = new JsonLoader(getActivity(), mNameCity);
		return loder;
	}

	@Override
	
	public void onLoadFinished(Loader<String> arg0, String strJson){
		
		if(strJson != null){
			try {
				setData(new JSONObject(strJson));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		getLoaderManager().destroyLoader(Constants.LOADER_TIME_ID);				
	}

	@Override
	public void onResume() {
		super.onResume();
		starGoogleMap();
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
				
	}	
	
	public void onCreateOptionsMenu(
	      Menu menu, MenuInflater inflater) {
	      inflater.inflate(R.menu.main, menu);
	      
	      SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		  searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		  
		    if (null != searchView) {
		        searchView.setSearchableInfo(searchManager
		        .getSearchableInfo(getActivity().getComponentName()));
		        searchView.setIconifiedByDefault(false);
		        searchView.setQueryHint("City Name");
		    }

		    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
		        public boolean onQueryTextChange(String newText) {
		            return true;
		        }

		        public boolean onQueryTextSubmit(String query) {		        	
		        		mNameCity = query;
		                isNetworkConnected();
		                searchView.clearFocus();
					return true;
		        }
		    };
		    
		    searchView.setOnQueryTextListener(queryTextListener);
	}
	
	public void starGoogleMap() {

		mapFragment = new SupportMapFragment() {

			@Override
			public void onActivityCreated(Bundle savedInstanceState) {
				super.onActivityCreated(savedInstanceState);

				mGoogleMap = mapFragment.getMap();
				if (mGoogleMap != null) {

					UiSettings uiSettings = mGoogleMap.getUiSettings();
					uiSettings.setZoomControlsEnabled(true);
					mGoogleMap.setMyLocationEnabled(true);
					CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(48.761043, 30.230563)).zoom(3).build();
					mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			}
		};

		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.map, mapFragment).addToBackStack(null).commit();
	}
	
	public void movingCamera(Location location) {

		CameraPosition position = CameraPosition.builder().bearing(location.getBearing()).target(new LatLng(location.getLatitude(),location.getLongitude()))
				.zoom(10).tilt(mGoogleMap.getCameraPosition().tilt).build();
		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
	}
}
