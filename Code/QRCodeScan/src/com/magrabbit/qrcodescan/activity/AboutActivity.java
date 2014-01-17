package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;

/**
 * @author vu le
 * 
 */
public class AboutActivity extends Activity implements MenuSlidingClickListener {

	private SlidingMenuCustom mMenu;
	private TextView mTvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mTvTitle = (TextView) findViewById(R.id.header_tv_title);
		mTvTitle.setText(R.string.header_title_about);

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
		startActivity(new Intent(this, ScanActivity.class));
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onHistoryClickListener() {
		startActivity(new Intent(this, HistoryActivity.class));
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onAboutClickListener() {
		// startActivity(new Intent(this,AboutActivity.class));
		// finish();
	}

	@Override
	public void onSettingClickListener() {
		startActivity(new Intent(this, SettingActivity.class));
		finish();
		overridePendingTransition(0, 0);
	}
}
