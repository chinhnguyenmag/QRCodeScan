package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.control.SwitchView;
import com.magrabbit.qrcodescan.control.SwitchView.OnSwitchChangeListener;
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
	private TextView mTvTitle;
	private ImageButton mBtRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mTvTitle = (TextView) findViewById(R.id.header_tv_title);
		mBtRight = (ImageButton) findViewById(R.id.header_bt_right);
		mTvTitle.setText(R.string.header_title_setting);
		mBtRight.setVisibility(View.GONE);

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
		if (mAppPreferences.getCloseUrlTime() == -1) {
			mTvTime.setText("Nerver");
		} else {
			mTvTime.setText(mAppPreferences.getCloseUrlTime() + " seconds");
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
		overridePendingTransition(0,0);
	}

	@Override
	public void onHistoryClickListener() {
		startActivity(new Intent(this, HistoryActivity.class));
		finish();
		overridePendingTransition(0,0);
	}

	@Override
	public void onAboutClickListener() {
		startActivity(new Intent(this, AboutActivity.class));
		finish();
		overridePendingTransition(0,0);
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
						if (mAppPreferences.getCloseUrlTime() == -1) {
							mTvTime.setText("Nerver");
						} else {
							mTvTime.setText(mAppPreferences.getCloseUrlTime()
									+ " seconds");
						}
					}

					@Override
					public void click_Cancel() {

					}
				}, mAppPreferences.getCloseUrlTime());
		d.show();
	}
}
