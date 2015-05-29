package com.example.weathermobile;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class JsonLoader extends AsyncTaskLoader<String> {
	
	

	public JsonLoader(Context context) {
		super(context);
		
	}

	@Override
	public String loadInBackground() {	
		return "ok";
	}

	
}
