package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appspheregroup.android.swichview.SwitchView;
import com.appspheregroup.android.swichview.SwitchView.OnSwitchChangeListener;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.DialogPickTime;
import com.magrabbit.qrcodescan.customview.DialogPickTime.ProcessDialogPickTime;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.AppPreferences;

public class SettingActivity extends Activity implements
		MenuSlidingClickListener, OnSwitchChangeListener {

	private SlidingMenuCustom mMenu;
	private SwitchView mSwitchViewSound;
	private SwitchView mSwitchViewOpenUrl;
	private AppPreferences mAppPreferences;
	private TextView mTvTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mAppPreferences = new AppPreferences(this);
		mMenu = new SlidingMenuCustom(this, this);
		mMenu.setTouchModeAboveMargin();
		mSwitchViewSound = (SwitchView) findViewById(R.id.activity_settings_sv_sound);
		mSwitchViewOpenUrl = (SwitchView) findViewById(R.id.activity_settings_sv_open_url);
		mTvTime = (TextView) findViewById(R.id.activity_setting_tv_time);

		mSwitchViewSound.setOnSwitchChangeListener(this);
		mSwitchViewOpenUrl.setOnSwitchChangeListener(this);

		mSwitchViewSound.setSwitchOn(mAppPreferences.isSound());
		mSwitchViewOpenUrl.setSwitchOn(mAppPreferences.isOpenUrl());
		if (mAppPreferences.getCloseUrl() == -1) {
			mTvTime.setText("Nerver");
		} else {
			mTvTime.setText(mAppPreferences.getCloseUrl() + " seconds");
		}
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

	}

	@Override
	public void onSwitchChanged(View view, boolean isOn) {

		switch (view.getId()) {
		case R.id.activity_settings_sv_sound:
			mAppPreferences.setSound(isOn);
			break;
		case R.id.activity_settings_sv_open_url:
			mAppPreferences.setOpenUrl(isOn);
			break;
		default:
			break;
		}
	}

	public void onClick_AutoClose(View v) {
		DialogPickTime d = new DialogPickTime(this,
				new ProcessDialogPickTime() {

					@Override
					public void click_Ok(int value) {
						mAppPreferences.setCloseUrl(value);
						if (mAppPreferences.getCloseUrl() == -1) {
							mTvTime.setText("Nerver");
						} else {
							mTvTime.setText(mAppPreferences.getCloseUrl()
									+ " seconds");
						}
					}

					@Override
					public void click_Cancel() {

					}
				}, mAppPreferences.getCloseUrl());
		d.show();
	}
}
