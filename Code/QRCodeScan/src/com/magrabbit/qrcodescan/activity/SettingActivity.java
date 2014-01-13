package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.appspheregroup.android.swichview.SwitchView;
import com.appspheregroup.android.swichview.SwitchView.OnSwitchChangeListener;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;

/**
 * @author vule
 * @description this class use to show all functional in
 *              application(sound,history,...)
 * 
 * 
 */
public class SettingActivity extends Activity implements
		MenuSlidingClickListener, OnSwitchChangeListener {

	private SlidingMenuCustom mMenu;
	private SwitchView mSwitchViewSound;
	private SwitchView mSwitchViewOpenUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mMenu = new SlidingMenuCustom(this, this);
		mSwitchViewSound = (SwitchView) findViewById(R.id.activity_settings_sv_sound);
		mSwitchViewOpenUrl = (SwitchView) findViewById(R.id.activity_settings_sv_open_url);
		mSwitchViewSound.setOnSwitchChangeListener(this);
		mSwitchViewOpenUrl.setOnSwitchChangeListener(this);
	}

	public void onClick_Menu(View view) {
		if (mMenu == null) {
			mMenu = new SlidingMenuCustom(this, this);
		}
		mMenu.toggle();
	}

	@Override
	public void onScannerClickListener() {
		startActivity(new Intent(this, ScanActivity.class));
		finish();

	}

	@Override
	public void onHistoryClickListener() {
		startActivity(new Intent(this, HistoryActivity.class));
		finish();

	}

	@Override
	public void onAboutClickListener() {
		startActivity(new Intent(this, AboutActivity.class));
		finish();
	}

	@Override
	public void onSettingClickListener() {
		// startActivity(new Intent(this,SettingActivity.class));
		// finish();

	}

	@Override
	public void onSwitchChanged(View view, boolean isOn) {
		
		switch (view.getId()) {
		case R.id.activity_settings_sv_sound:
			
			break;
		case R.id.activity_settings_sv_open_url:
			
			break;
		default:
			break;
		}

	}
}
