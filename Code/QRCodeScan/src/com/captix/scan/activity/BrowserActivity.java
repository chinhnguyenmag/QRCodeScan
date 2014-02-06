package com.captix.scan.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.captix.scan.R;
import com.captix.scan.model.AppPreferences;
import com.captix.scan.utils.StringExtraUtils;

public class BrowserActivity extends BaseActivity {

	private String mScanResult;
	private WebView mWebView;
	private TextView mTvTitle;
	private ProgressBar mProgressLoading;
	// Application Preference
	private AppPreferences mPreference;

	// Closing Web Page
	private int mCloseTime;
	private Timer mTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website);
		try {

			// Get the setting of time of opening URL
			mPreference = new AppPreferences(BrowserActivity.this);
			mCloseTime = mPreference.getCloseUrlTime();

			mTvTitle = (TextView) findViewById(R.id.header_website_tv_title);
			mProgressLoading = (ProgressBar) findViewById(R.id.activity_website_pb_loading);
			mProgressLoading.setVisibility(View.GONE);
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				mScanResult = bundle
						.getString(StringExtraUtils.KEY_SCAN_RESULT);
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
					mProgressLoading.setVisibility(View.GONE);
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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onClick_Back(View v) {
		finish();
	}

	public void onClick_Share(View v) {
		try {
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Share QR Code");
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					mScanResult);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}