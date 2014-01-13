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

	public boolean setSound(boolean isSound) {
		mPrefsEditor.putBoolean("Sound", isSound);
		mPrefsEditor.commit();
		return true;
	}

	public boolean isSound() {
		boolean isSound = mAppSharedPrefs.getBoolean("Sound", false);
		return isSound;
	}

	public boolean setOpenUrl(boolean isOpen) {
		mPrefsEditor.putBoolean("OpenUrl", isOpen);
		mPrefsEditor.commit();
		return true;
	}

	public boolean isOpenUrl() {
		boolean isOpen = mAppSharedPrefs.getBoolean("OpenUrl", false);
		return isOpen;
	}

	public boolean setProfileUrl(String profileUrl) {
		mPrefsEditor.putString("ProfileUrl", profileUrl);
		mPrefsEditor.commit();
		return true;
	}

	public String getProfileUrl() {
		String profileUrl = mAppSharedPrefs.getString("ProfileUrl", "");
		return profileUrl;
	}

	public boolean setCloseUrl(boolean isClose) {
		mPrefsEditor.putBoolean("CloseUrl", isClose);
		mPrefsEditor.commit();
		return true;
	}

	public boolean getCloseUrl() {
		boolean isClose = mAppSharedPrefs.getBoolean("CloseUrl", false);
		return isClose;
	}
}
