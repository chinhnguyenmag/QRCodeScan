package com.captix.scan.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.captix.scan.R;
import com.captix.scan.model.AppPreferences;
import com.captix.scan.utils.StringExtraUtils;

public class BrowserActivity extends BaseActivity {

	private String mScanResult;
	private WebView mWebView;
	private TextView mTvTitle;
	// Application Preference
	private AppPreferences mPreference;

	// Closing Web Page
	private int mCloseTime;
	private Timer mTimer;
	private ImageButton mIbShortcus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website);
		try {

			// Get the setting of time of opening URL
			mPreference = new AppPreferences(BrowserActivity.this);
			mCloseTime = mPreference.getCloseUrlTime();

			mTvTitle = (TextView) findViewById(R.id.header_website_tv_title);

			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				mScanResult = bundle
						.getString(StringExtraUtils.KEY_SCAN_RESULT);
			}
			mWebView = (WebView) findViewById(R.id.activity_website_wv);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.setWebViewClient(new WebViewClient());
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDomStorageEnabled(true);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setDomStorageEnabled(true);
			mWebView.getSettings().setPluginState(PluginState.ON);
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
					getString(R.string.email_title_share));
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					mScanResult);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
