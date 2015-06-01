package com.example.weathermobile.fragment;

import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.weathermobile.Constants;
import com.example.weathermobile.JsonHandler;
import com.example.weathermobile.JsonLoader;
import com.example.weathermobile.LogicPart;
import com.example.weathermobile.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

	private TextView mTempTextView, mPressureTextView, mWindTextView, mHumidityTextView, mNameCityTextView;
	private ImageView mWeatherIcon;
	private String mNameCity;
	private DecimalFormat dec = new DecimalFormat("0.00");
	private JsonHandler mJsonHandler = new JsonHandler();
	private Location mCityLocation = new Location("city location");
	private SearchView searchView;
	private SupportMapFragment mapFragment;
	private GoogleMap mGoogleMap;
	private LogicPart mLogicPart = new LogicPart();
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		    setHasOptionsMenu(true);
	    		
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		
		return view;
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
				infoWindowAdapter(new JSONObject(strJson));
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
		
	private void starGoogleMap() {

		mapFragment = new SupportMapFragment() {
			@Override
			public void onActivityCreated(Bundle savedInstanceState) {
				super.onActivityCreated(savedInstanceState);

				mGoogleMap = mapFragment.getMap();
				if (mGoogleMap != null) {

					UiSettings uiSettings = mGoogleMap.getUiSettings();
					uiSettings.setZoomControlsEnabled(true);
					CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(48.761043, 30.230563)).zoom(3).build();
					mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			}
		};

		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.map, mapFragment).addToBackStack(null).commit();
	}	
		
	private void infoWindowAdapter(JSONObject jObj) throws JSONException{
		
		mCityLocation.setLatitude(mJsonHandler.getDoubleSubObj(jObj, "coord", "lat"));
		mCityLocation.setLongitude(mJsonHandler.getDoubleSubObj(jObj, "coord", "lon"));		
		mLogicPart.setmTemp(String.valueOf(dec.format(mJsonHandler.getDoubleSubObj(jObj, "main", "temp")- 273.15)+ " "+ getString(R.string.grad)));
		mLogicPart.setmWingSpeed(String.valueOf(mJsonHandler.getDoubleSubObj(jObj,"wind", "speed")) + " " + getString(R.string.win));
		mLogicPart.setmHumidity(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "humidity")) + " " + getString(R.string.hum));
		mLogicPart.setmPressure(String.valueOf(mJsonHandler.getDoubleSubObj(jObj, "main", "pressure")) + " " + getString(R.string.pres));
		mLogicPart.setmIconUrl(String.valueOf(mJsonHandler.getStringArrey(jObj, "weather", "icon")));
		mLogicPart.setmNameCity(mJsonHandler.getStringleObj(jObj, "name"));
		
		CameraPosition position = CameraPosition.builder().bearing(mCityLocation.getBearing()).target(new LatLng(mCityLocation.getLatitude(),mCityLocation.getLongitude())).zoom(10).tilt(mGoogleMap.getCameraPosition().tilt).build();		
		Marker melbourne = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mCityLocation.getLatitude(),mCityLocation.getLongitude())));		
		
		mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				
				View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_information_weather, null);
				
				mTempTextView = (TextView) v.findViewById(R.id.temp_TextView);
				mPressureTextView = (TextView) v.findViewById(R.id.pressure_TextView);
				mWindTextView = (TextView) v.findViewById(R.id.wind_TextView);
				mWeatherIcon = (ImageView) v.findViewById(R.id.weatherIcon);
				mHumidityTextView = (TextView) v.findViewById(R.id.humidity_TextView);
				mNameCityTextView = (TextView) v.findViewById(R.id.nameCity);
				
				mTempTextView.setText(mLogicPart.getmTemp());
				mPressureTextView.setText(mLogicPart.getmPressure());
				mWindTextView.setText(mLogicPart.getmWingSpeed());
				mHumidityTextView.setText(mLogicPart.getmHumidity());
				mNameCityTextView.setText(mLogicPart.getmNameCity());
				Picasso.with(getActivity()).load(Constants.ICON_URL	+ mLogicPart.getmIconUrl() + ".png").into(mWeatherIcon);
				
				return v;
			}
		});
		
		melbourne.showInfoWindow();
		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
	}
}
