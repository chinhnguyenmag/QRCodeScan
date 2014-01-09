package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;

/**
 * @author vu le
 *
 */
public class AboutActivity extends Activity implements MenuSlidingClickListener {

	private SlidingMenuCustom mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mMenu = new SlidingMenuCustom(this, this);
	}
	
	public void onClick_Menu(View view) {
		if (mMenu == null) {
			mMenu = new SlidingMenuCustom(this, this);
		}
		mMenu.toggle();
	}

	@Override
	public void onScannerClickListener() {
		startActivity(new Intent(this,ScanActivity.class));
		finish();

	}

	@Override
	public void onHistoryClickListener() {
		startActivity(new Intent(this,HistoryActivity.class));
		finish();

	}

	@Override
	public void onAboutClickListener() {
		startActivity(new Intent(this,AboutActivity.class));
		finish();
	}

	@Override
	public void onSettingClickListener() {
		startActivity(new Intent(this,SettingActivity.class));
		finish();
		
	}
}
