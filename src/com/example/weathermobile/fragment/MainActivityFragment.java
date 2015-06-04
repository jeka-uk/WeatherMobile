package com.example.weathermobile.fragment;

import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.weathermobile.Constants;
import com.example.weathermobile.JsonHandler;
import com.example.weathermobile.JsonLoader;
import com.example.weathermobile.R;
import com.google.android.gms.internal.bn;
import com.google.android.gms.internal.gm;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
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
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityFragment extends Fragment implements LoaderCallbacks<String>, OnMapReadyCallback {

	private static final String LOG_TAG = "myLogs";

	private TextView mTempTextView, mPressureTextView, mWindTextView, mHumidityTextView, mNameCityTextView, mNameOfCountry;
	private ImageView mWeatherIcon;
	private String mNameCity, mStrJson;
	private DecimalFormat dec = new DecimalFormat("0.00");
	private JsonHandler mJsonHandler = new JsonHandler();
	private Location mCityLocation = new Location("city location");
	private SearchView searchView;
	private GoogleMap mGoogleMap;
	private JSONObject mJSONObject;	
	private boolean res = false;
				
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		    setHasOptionsMenu(true);
	    		
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		
		return view;
	}
	
	private void startLoder(){
	    
	    getLoaderManager().initLoader(Constants.LOADER_TIME_ID, null, this).forceLoad();   
	    
	}

	private boolean isNetworkConnected() {
	    
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNinfo = cm.getActiveNetworkInfo();
		
		if (mNinfo == null) {
		    
			showMessage("No internet connection");			
			return false;

		} else {
		    
		    return true;		    
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
			    
			    mStrJson = strJson;		
				mJSONObject = new JSONObject(mStrJson);
				infoWindowAdapter();
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		getLoaderManager().destroyLoader(Constants.LOADER_TIME_ID);		
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
		           	            
		            if(isNetworkConnected() == true || query != null ){
		                
		                mNameCity = query;
		                startLoder();
                        searchView.clearFocus();
		            }
		            
					return true;
		        }
		    };
		    
		    searchView.setOnQueryTextListener(queryTextListener);
	}	
		
	private void starGoogleMap() {
	    
	    MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
	            .findFragmentById(R.id.map);
	        mapFragment.getMapAsync(this);
	}	
		
	private void infoWindowAdapter() throws JSONException{	    
        
        mGoogleMap.clear();
        
        
		mCityLocation.setLatitude(mJsonHandler.getDoubleSubObj(mJSONObject, "coord", "lat"));
		mCityLocation.setLongitude(mJsonHandler.getDoubleSubObj(mJSONObject, "coord", "lon"));
		
		CameraPosition position = CameraPosition.builder().bearing(mCityLocation.getBearing()).target(new LatLng(mCityLocation.getLatitude(),mCityLocation.getLongitude())).zoom(10).tilt(mGoogleMap.getCameraPosition().tilt).build();		
		Marker melbourne = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mCityLocation.getLatitude(),mCityLocation.getLongitude())));
		melbourne.showInfoWindow();
				
		mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker)  {
				
				View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_information_weather, null);
				
				mTempTextView = (TextView) v.findViewById(R.id.temp_TextView);
				mPressureTextView = (TextView) v.findViewById(R.id.pressure_TextView);
				mWindTextView = (TextView) v.findViewById(R.id.wind_TextView);
				mWeatherIcon = (ImageView) v.findViewById(R.id.weatherIcon);
				mHumidityTextView = (TextView) v.findViewById(R.id.humidity_TextView);
				mNameCityTextView = (TextView) v.findViewById(R.id.nameCity_TextView);
				mNameOfCountry = (TextView) v.findViewById(R.id.nameOfCountry_TextView);
				
				
	try {	    
	    
	            Picasso.with(getActivity()).load(Constants.ICON_URL  + mJsonHandler.getStringArrey(mJSONObject, "weather", "icon") + ".png").into(mWeatherIcon);
			    mTempTextView.setText(String.valueOf(dec.format(mJsonHandler.getDoubleSubObj(mJSONObject, "main", "temp")- 273.15)+ " "));
				mPressureTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(mJSONObject, "main", "pressure")) + " " + getString(R.string.pres));
				mWindTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(mJSONObject,"wind", "speed")) + " " + getString(R.string.win));
				mHumidityTextView.setText(String.valueOf(mJsonHandler.getDoubleSubObj(mJSONObject, "main", "humidity")) + " " + getString(R.string.hum));
				mNameCityTextView.setText(mJsonHandler.getStringleObj(mJSONObject, "name")+",");
				mNameOfCountry.setText(mJsonHandler.getStringSubObj(mJSONObject, "sys", "country"));
								
				
		     } catch (JSONException e) {
					e.printStackTrace();
			}	
				return v;
			}
		});
		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
	}
   
    @Override
    public void onResume() {
        super.onResume();
        starGoogleMap();
        
        Log.d(LOG_TAG, "res: " + res);
        
    }
    
    

    @Override
    public void onPause() {
        super.onPause();
        
        res = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        
        if(googleMap != null){
            mGoogleMap = googleMap;
            UiSettings uiSettings = mGoogleMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
        }
        
    }
    
}