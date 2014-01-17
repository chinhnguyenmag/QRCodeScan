package com.magrabbit.qrcodescan.activity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.control.DatabaseHandler;
import com.magrabbit.qrcodescan.customview.CameraPreviewNew;
import com.magrabbit.qrcodescan.customview.DialogConfirm;
import com.magrabbit.qrcodescan.customview.DialogConfirm.ProcessDialogConfirm;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.AppPreferences;
import com.magrabbit.qrcodescan.model.QRCode;
import com.magrabbit.qrcodescan.utils.StringExtraUtils;
import com.magrabbit.qrcodescan.utils.ZBarConstants;

public class ScanActivity extends Activity implements Camera.PreviewCallback,
		ZBarConstants, MenuSlidingClickListener {

	// private CameraPreview mPreview;
	private CameraPreviewNew mPreview;
	public Camera mCamera;
	private ImageScanner mScanner;
	private Handler mAutoFocusHandler;
	private boolean mPreviewing = true;
	private RelativeLayout mFrameCamera;
	// Application Preference
	private AppPreferences mPreference;
	// For Sliding Menu
	private SlidingMenuCustom mSlidingMenu;
	// Save scanned QRCode into local database by SQLite
	private DatabaseHandler mDataHandler;
	private TextView mTvTitle;
	private ImageButton mBtRight;
	private AlertDialog.Builder alertDialogBuilder;

	static {
		System.loadLibrary("iconv");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		mTvTitle = (TextView) findViewById(R.id.header_tv_title);
		mBtRight = (ImageButton) findViewById(R.id.header_bt_right);
		mTvTitle.setText(R.string.header_title_scan);
		mBtRight.setVisibility(View.GONE);

		mPreference = new AppPreferences(ScanActivity.this);

		mDataHandler = new DatabaseHandler(this);

		mFrameCamera = (RelativeLayout) findViewById(R.id.activity_scan_camera);
		
		if (!isCameraAvailable()) {
			// Cancel request if there is no rear-facing camera.
			cancelRequest();
			return;
		}

		mAutoFocusHandler = new Handler();

		// Create and configure the ImageScanner;
		setupScanner();

		// Create a RelativeLayout container that will hold a SurfaceView,
		// and set it as the content of our activity.

		// mPreview = new CameraPreview(this, this, autoFocusCB);
		mPreview = new CameraPreviewNew(this, this, autoFocusCB);

		mFrameCamera.addView(mPreview);
		mSlidingMenu = new SlidingMenuCustom(this, this);
	}

	public void setupScanner() {
		mScanner = new ImageScanner();
		mScanner.setConfig(0, Config.X_DENSITY, 3);
		mScanner.setConfig(0, Config.Y_DENSITY, 3);

		int[] symbols = new int[] { Symbol.QRCODE };
		if (symbols != null) {
			mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
			for (int symbol : symbols) {
				mScanner.setConfig(symbol, Config.ENABLE, 1);
			}
		}
	}

	@Override
	protected void onResume() {
		try {
			super.onResume();
			// Open the default i.e. the first rear facing camera.
			mCamera = Camera.open();
			if (mCamera == null) {
				// Cancel request if mCamera is null.
				cancelRequest();
				return;
			}

			mPreview.setCamera(mCamera);
			/* Please Uncomment */
			// mPreview.showSurfaceView();

			mPreviewing = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.cancelAutoFocus();
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();

			/* Please Uncomment */
			// mPreview.hideSurfaceView();

			mPreviewing = false;
			mCamera = null;
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	public void cancelRequest() {
		Intent dataIntent = new Intent();
		dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
		setResult(Activity.RESULT_CANCELED, dataIntent);
		finish();
	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		Camera.Size size = parameters.getPreviewSize();

		Image barcode = new Image(size.width, size.height, "Y800");
		barcode.setData(data);

		int result = mScanner.scanImage(barcode);

		if (result != 0) {
			// Turn off Camera Preview
			mCamera.stopPreview();
			mPreviewing = false;
			SymbolSet syms = mScanner.getResults();
			for (Symbol sym : syms) {
				final String symData = sym.getData();
				if (!TextUtils.isEmpty(symData)) {
					if (symData.contains("cptr.it/?var=")
							&& symData.contains("&id=")) {
						// Save into Database
						Format formatter = new SimpleDateFormat(
								"EEE, MMM dd yyyy");

						String date = formatter.format(new Date());
						mDataHandler.addQRCode(new QRCode(date, symData));

						// Get the QR Code after scanning and put it to Browser
						// for
						// searching on WebSite

						// Check whether to play sound or not
						if (mPreference.isSound()) {
							playSound();
						}

						if (mPreference.isOpenUrl()) {
							// Stop scanning
							mCamera.cancelAutoFocus();
							mCamera.setPreviewCallback(null);

							Intent dataIntent = new Intent(ScanActivity.this,
									BrowserActivity.class);
							dataIntent.putExtra(
									StringExtraUtils.KEY_SCAN_RESULT, symData);
							startActivity(dataIntent);
						} else {
							// Stop scanning
							mCamera.cancelAutoFocus();
							mCamera.setPreviewCallback(null);

							DialogConfirm dialog = new DialogConfirm(
									ScanActivity.this,
									android.R.drawable.ic_dialog_alert,
									ScanActivity.this
											.getString(R.string.activity_scan_open_browser_title),
									ScanActivity.this
											.getString(R.string.activity_scan_open_url_confirm),
									true, new ProcessDialogConfirm() {

										@Override
										public void click_Ok() {

											Intent dataIntent = new Intent(
													ScanActivity.this,
													BrowserActivity.class);
											dataIntent
													.putExtra(
															StringExtraUtils.KEY_SCAN_RESULT,
															symData);
											startActivity(dataIntent);

										}

										@Override
										public void click_Cancel() {
											// Start scanning again
											mCamera.setPreviewCallback(ScanActivity.this);
										}
									});
							dialog.show();
						}
						break;

					} else {
						// Show warning dialog for Invalid URL
						showComingSoonDialog(ScanActivity.this);
						// Stop scanning
						mCamera.cancelAutoFocus();
						mCamera.setPreviewCallback(null);
					}
				}
			}
			// Turn on Camera Preview
			mCamera.startPreview();
			mPreviewing = true;

		}
	}

	/* INVALID URL DIALOG */

	public void showComingSoonDialog(Context context) {
		alertDialogBuilder = new AlertDialog.Builder(context);

		// set title
		alertDialogBuilder.setTitle(context.getResources().getString(
				R.string.activity_scan_invalid_url_title));

		// set dialog message
		alertDialogBuilder
				.setMessage(
						context.getResources()
								.getString(
										R.string.activity_scan_invalid_url_message))
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						startPreviewAgain();
					}
				}); // create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void startPreviewAgain() {
		// Turn on Camera Preview
		mCamera.startPreview();
		mPreviewing = true;
		// Start scanning again
		mCamera.setPreviewCallback(ScanActivity.this);
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (mCamera != null && mPreviewing) {
				mCamera.autoFocus(autoFocusCB);
			}
		}
	};

	// Mimic continuous auto-focusing
	Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	private void playSound() {
		try {
			MediaPlayer mp = MediaPlayer.create(getBaseContext(),
					R.raw.camera_shutter);
			mp.start();

			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPreference != null) {
			mPreference = null;
		}
	}

	public void onClick_Menu(View view) {
		if (mSlidingMenu == null) {
			mSlidingMenu = new SlidingMenuCustom(this, this);
		}
		mSlidingMenu.toggle();
	}

	@Override
	public void onScannerClickListener() {
		mSlidingMenu.toggle();
	}

	@Override
	public void onHistoryClickListener() {
		startActivity(new Intent(ScanActivity.this, HistoryActivity.class));
		finish();
		overridePendingTransition(0,0);
	}

	@Override
	public void onAboutClickListener() {
		startActivity(new Intent(this, AboutActivity.class));
		finish();
		overridePendingTransition(0,0);
	}

	@Override
	public void onSettingClickListener() {
		startActivity(new Intent(ScanActivity.this, SettingActivity.class));
		finish();
		overridePendingTransition(0,0);
	}
}
