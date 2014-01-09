package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.os.Bundle;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;

public class AboutActivity extends Activity implements MenuSlidingClickListener {

	private SlidingMenuCustom mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mMenu = new SlidingMenuCustom(this, this);
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
