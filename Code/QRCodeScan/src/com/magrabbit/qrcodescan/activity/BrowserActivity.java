package com.magrabbit.qrcodescan.activity;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.utils.StringExtraUtils;

public class BrowserActivity extends ParentActivity {

	private String mScanResult;
	private WebView mWebView;
	private TextView mTvTitle;
	URL url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website);
		mTvTitle = (TextView) findViewById(R.id.header_website_tv_title);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mScanResult = bundle.getString(StringExtraUtils.KEY_SCAN_RESULT);
		}
		mWebView = (WebView) findViewById(R.id.activity_website_wv);
		try {
			url = new URL("www.slitaz.org");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mWebView.loadUrl(url.toString());
		mWebView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				mTvTitle.setText(view.getTitle());
			}
		});
	}

	/**
	 * @param v
	 * @Description Processing login and adding a new EverNote
	 */
	// public void addEverNote(View v) {
	// mEvernoteSession.authenticate(BrowserActivity.this);
	// }

	/**
	 * Called when the control returns from an activity that we launched.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		// Add a new EverNote when OAuth activity returns result
		case EvernoteSession.REQUEST_CODE_OAUTH:
			if (resultCode == Activity.RESULT_OK) {
				startActivity(new Intent(BrowserActivity.this,
						CreateEverNote.class));
			}
			break;
		}
	}

	public void onClick_Back(View v) {

	}

}
