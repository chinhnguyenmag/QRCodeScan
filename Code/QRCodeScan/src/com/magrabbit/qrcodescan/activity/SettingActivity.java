package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.os.Bundle;

import com.magrabbit.qrcodescan.R;

/**
 * @author vule
 * @description this class use to show all functional in application(sound,history,...)
 * 
 *
 */
public class SettingActivity extends Activity { 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

}
