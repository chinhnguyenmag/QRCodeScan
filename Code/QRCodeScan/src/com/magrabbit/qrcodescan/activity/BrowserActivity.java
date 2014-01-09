package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.os.Bundle;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.utils.StringExtraUtils;

public class BrowserActivity extends Activity {

	private String mScanResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mScanResult = bundle.getString(StringExtraUtils.KEY_SCAN_RESULT);
		}
	}

}
