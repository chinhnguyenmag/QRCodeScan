package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.utils.Utils;

/**
 * @author vu le
 * 
 */
public class AboutActivity extends Activity implements MenuSlidingClickListener {

	private SlidingMenuCustom mMenu;
	private TextView mTvTitle;
	WebView mWvContent;
	String mContent = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mTvTitle = (TextView) findViewById(R.id.header_tv_title);
		mTvTitle.setText(R.string.header_title_about);

		mContent = Utils.getHtmlFromAsset(this, R.string.content_html);
		mWvContent = (WebView) findViewById(R.id.about_wv_introduce);
		mWvContent.setBackgroundColor(0x00000000);

		mWvContent.loadDataWithBaseURL("file:///android_asset", mContent,
				"text/html", "UTF-8", null);

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
