package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.control.SwitchView;
import com.magrabbit.qrcodescan.control.SwitchView.OnSwitchChangeListener;
import com.magrabbit.qrcodescan.customview.DialogPickTime;
import com.magrabbit.qrcodescan.customview.DialogPickTime.ProcessDialogPickTime;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.AppPreferences;

public class SettingActivity extends Activity implements
		MenuSlidingClickListener, OnSwitchChangeListener, OnClickListener {

	private SlidingMenuCustom mMenu;
	private SwitchView mSwitchViewSound;
	private SwitchView mSwitchViewOpenUrl;
	private AppPreferences mAppPreferences;
	private TextView mTvTime;
	private TextView mTvTitle;
	private ImageButton mBtRight;
	private RelativeLayout mRlShareFacebook;
	private RelativeLayout mRlShareSMS;
	private RelativeLayout mRlShareMail;
	private RelativeLayout mRlShareTwitter;

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
		mRlShareFacebook = (RelativeLayout) findViewById(R.id.setting_ll_social_facebook);
		mRlShareSMS = (RelativeLayout) findViewById(R.id.setting_ll_social_message);
		mRlShareMail = (RelativeLayout) findViewById(R.id.setting_ll_social_mail);
		mRlShareTwitter = (RelativeLayout) findViewById(R.id.setting_ll_social_twitter);
		mRlShareSMS.setOnClickListener(this);
		mRlShareMail.setOnClickListener(this);
		mRlShareTwitter.setOnClickListener(this);
		mRlShareFacebook.setOnClickListener(this);

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_ll_social_message: {
			Toast.makeText(this, "Share via SMS", Toast.LENGTH_SHORT).show();
			break;
		}

		case R.id.setting_ll_social_mail: {
			Toast.makeText(this, "Share via Mail", Toast.LENGTH_SHORT).show();
			break;
		}

		case R.id.setting_ll_social_twitter: {
			Toast.makeText(this, "Share via Twitter", Toast.LENGTH_SHORT)
					.show();
			break;
		}

		case R.id.setting_ll_social_facebook: {
			Toast.makeText(this, "Share via Facebook", Toast.LENGTH_SHORT)
					.show();
			break;
		}

		default:
			break;
		}
	}
}
