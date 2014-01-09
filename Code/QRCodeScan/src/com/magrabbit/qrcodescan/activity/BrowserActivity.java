package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.utils.StringExtraUtils;

public class BrowserActivity extends ParentActivity {

	private String mScanResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mScanResult = bundle.getString(StringExtraUtils.KEY_SCAN_RESULT);
		}

	}

	/**
	 * @param v
	 * @Description Processing login and adding a new EverNote
	 */
	public void addEverNote(View v) {
		mEvernoteSession.authenticate(BrowserActivity.this);
	}

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

}
