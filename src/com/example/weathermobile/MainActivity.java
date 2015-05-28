package com.example.weathermobile;



import com.example.weathermobile.fragment.MainActivityFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends SingleFragmentActivity {
	protected Fragment createFragment() {
		return new MainActivityFragment();
	}
}