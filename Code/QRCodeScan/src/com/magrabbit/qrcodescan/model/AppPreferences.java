package com.magrabbit.qrcodescan.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
	private final String APP_SHARED_PREFS = "MyMenuConfiguration";
	private SharedPreferences mAppSharedPrefs;
	private Editor mPrefsEditor;

	public AppPreferences(Context context) {
		this.mAppSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
				Context.MODE_PRIVATE);
		this.mPrefsEditor = mAppSharedPrefs.edit();
	}

	public boolean setServerURL(String serverUrl) {
		mPrefsEditor.putString("Server", serverUrl);
		mPrefsEditor.commit();
		return true;
	}

	public String getServerURL() {
		String server = mAppSharedPrefs.getString("Server", "");
		return server;
	}
}
