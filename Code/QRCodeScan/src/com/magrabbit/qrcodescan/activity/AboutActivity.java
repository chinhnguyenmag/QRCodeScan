package com.magrabbit.qrcodescan.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.utils.Utils;

/**
 * @author vu le
 * 
 */
public class AboutActivity extends BaseActivity implements
		MenuSlidingClickListener {

	private SlidingMenuCustom mMenu;
	private TextView mTvTitle;
	WebView mWvContent;
	String mContent = "";
	private long lastPressedTime;
	private static final int PERIOD = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mTvTitle = (TextView) findViewById(R.id.header_tv_title);
		mTvTitle.setText(R.string.header_title_about);

		mWvContent = (WebView) findViewById(R.id.about_wv_introduce);
		setTransparentBackground();
		mMenu = new SlidingMenuCustom(this, this);

		new loadTask().execute();
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

	public class loadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgress();
		}

		@Override
		protected Void doInBackground(Void... params) {

			mContent = Utils.getHtmlFromAsset(AboutActivity.this,
					R.string.content_html);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mWvContent.loadDataWithBaseURL("file:///android_asset", mContent,
					"text/html", "UTF-8", null);
			dismissProgress();
		}

	}

	public void setTransparentBackground() {
		mWvContent.setBackgroundColor(0x00000000);
		mWvContent.getSettings().setLayoutAlgorithm(
				LayoutAlgorithm.SINGLE_COLUMN);
		mWvContent.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			switch (event.getAction()) {
			case KeyEvent.ACTION_DOWN:
				if (event.getDownTime() - lastPressedTime < PERIOD) {
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Press again to exit.", Toast.LENGTH_SHORT).show();
					lastPressedTime = event.getEventTime();
				}
				return true;
			}
		}
		return false;
	}
}
