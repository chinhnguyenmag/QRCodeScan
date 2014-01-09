package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customize.MySwitch;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;

/**
 * @author vule
 * @description this class use to show all functional in
 *              application(sound,history,...)
 * 
 * 
 */
public class SettingActivity extends Activity implements MenuSlidingClickListener{

	private MySwitch mySwitchOnOffSound;
	private MySwitch mySwitchOnOffOpenUrl;
	private SlidingMenuCustom mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mMenu = new SlidingMenuCustom(this, this);
		mySwitchOnOffSound = (MySwitch) findViewById(R.id.settings_switch_sound);
		mySwitchOnOffOpenUrl = (MySwitch) findViewById(R.id.settings_switch_open_url);
		mySwitchOnOffSound
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Toast.makeText(SettingActivity.this, "Sound ON",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(SettingActivity.this, "Sound OFF",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		mySwitchOnOffOpenUrl
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Toast.makeText(SettingActivity.this, "Open ON",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(SettingActivity.this, "Open OFF",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	@Override
	public void onScannerClickListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHistoryClickListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAboutClickListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSettingClickListener() {
		// TODO Auto-generated method stub
		
	}
}
