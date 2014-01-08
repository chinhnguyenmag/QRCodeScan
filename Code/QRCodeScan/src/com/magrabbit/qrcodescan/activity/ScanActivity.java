package com.magrabbit.qrcodescan.activity;

import net.sourceforge.zbar.Symbol;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.magrabbit.qrcodescan.utils.CodeRequest;

public class ScanActivity extends Activity {

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_scan);
		launchQRScanner(null);
	}

	public void launchQRScanner(View v) {
		if (isCameraAvailable()) {
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			intent.putExtra(ZBarConstants.SCAN_MODES,
					new int[] { Symbol.QRCODE });
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else {
			Toast.makeText(this, "Rear Facing Camera Unavailable",
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ZBAR_SCANNER_REQUEST:
		case ZBAR_QR_SCANNER_REQUEST:
			if (resultCode == RESULT_OK) {
				Toast.makeText(
						this,
						"Scan Result = "
								+ data.getStringExtra(ZBarConstants.SCAN_RESULT),
						Toast.LENGTH_LONG).show();

				// Change to Browser for searching result
				Intent intent = new Intent(ScanActivity.this,
						BrowserActivity.class);
				startActivityForResult(intent,
						CodeRequest.CODE_REQUEST_SCAN_ACTIVITY);

			} else if (resultCode == RESULT_CANCELED && data != null) {
				String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
				if (!TextUtils.isEmpty(error)) {
					Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case CodeRequest.CODE_REQUEST_SCAN_ACTIVITY:
			if (resultCode == RESULT_CANCELED) {
				startActivity(new Intent(ScanActivity.this, ScanActivity.class));
				finish();
			}
			break;
		}
	}
}
