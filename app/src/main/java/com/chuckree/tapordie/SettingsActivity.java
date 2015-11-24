package com.chuckree.tapordie;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.view.Window;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(com.chuckree.tapordie.R.xml.preferences);
		setTheme(R.style.Theme_AppCompat_LightText);
        getListView().setBackgroundColor(Color.rgb(255, 83, 118));
        SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        PreferenceCategory mCategory = (PreferenceCategory) findPreference("game_settings");
        ListPreference pref = (ListPreference) findPreference("speed");
        Log.d("Settings Activity", prefs.getBoolean(MainActivity.GAME_MODE_KEY, false) ? "Pro Mode" : "Kid Mode");
        if(!prefs.getBoolean(MainActivity.GAME_MODE_KEY, false)) mCategory.removePreference(pref);
	}
}