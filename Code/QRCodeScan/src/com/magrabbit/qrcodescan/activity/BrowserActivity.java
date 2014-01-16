package com.magrabbit.qrcodescan.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.model.AppPreferences;
import com.magrabbit.qrcodescan.utils.StringExtraUtils;

public class BrowserActivity extends BaseActivity {

	private String mScanResult;
	private WebView mWebView;
	private TextView mTvTitle;
	// Application Preference
	private AppPreferences mPreference;

	// Closing Web Page
	private int mCloseTime;
	private Timer mTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website);
		// Get the setting of time of opening URL
		mPreference = new AppPreferences(BrowserActivity.this);
		mCloseTime = mPreference.getCloseUrlTime();

		mTvTitle = (TextView) findViewById(R.id.header_website_tv_title);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mScanResult = bundle.getString(StringExtraUtils.KEY_SCAN_RESULT);
		}
		mWebView = (WebView) findViewById(R.id.activity_website_wv);
		if (mScanResult != null
				&& !mScanResult.toLowerCase().startsWith("http://")
				&& !mScanResult.toLowerCase().startsWith("https://")) {
			mScanResult = "http://" + mScanResult;
		}
		mWebView.loadUrl(mScanResult);

		mWebView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				mTvTitle.setText(view.getTitle());

				if (mCloseTime != -1) {
					// Timer for counting the amount of time to close
					// application
					mTimer = new Timer();
					TimerTask closeWebPage = new TimerTask() {

						@Override
						public void run() {
							finish();
						}
					};
					mTimer.schedule(closeWebPage, mCloseTime * 1000);
				}
			}
		});

	}

	public void onClick_Back(View v) {
		finish();
	}

	public void onClick_Bookmark(View v) {

	}

}
