package com.captix.scan.activity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.captix.scan.R;
import com.captix.scan.control.DatabaseHandler;
import com.captix.scan.customview.CameraPreviewNew;
import com.captix.scan.customview.DialogConfirm;
import com.captix.scan.customview.DialogConfirm.ProcessDialogConfirm;
import com.captix.scan.customview.SlidingMenuCustom;
import com.captix.scan.listener.MenuSlidingClickListener;
import com.captix.scan.model.AppPreferences;
import com.captix.scan.model.QRCode;
import com.captix.scan.utils.StringExtraUtils;
import com.captix.scan.utils.ZBarConstants;

public class ScanActivity extends Activity implements Camera.PreviewCallback,
		ZBarConstants, MenuSlidingClickListener {

	// private CameraPreview mPreview;
	private CameraPreviewNew mPreview;
	public Camera mCamera;
	private ImageScanner mScanner;
	private Handler mAutoFocusHandler;
	private boolean mPreviewing = true;
	private FrameLayout mFrameCamera;
	// Application Preference
	// For Sliding Menu
	private SlidingMenuCustom mMenu;
	// Save scanned QRCode into local database by SQLite
	private DatabaseHandler mDataHandler;
	private TextView mTvTitle;
	private AlertDialog.Builder alertDialogBuilder;
	private AlertDialog alertDialog;
	private long lastPressedTime;
	private static final int PERIOD = 2000;
	private AppPreferences mAppPreferences;
	private AudioManager mAudio;

	static {
		System.loadLibrary("iconv");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		mAudio = (AudioManager) getSystemService(this.AUDIO_SERVICE);
		mAppPreferences = new AppPreferences(this);
		mAppPreferences.setOpenUrl(true);
		if (mAppPreferences.getProfileUrl().equals("")) {
			mAppPreferences.setProfileUrl("cptr.it/?var={variable}&id=test");
		}
		try {
			mTvTitle = (TextView) findViewById(R.id.header_tv_title);
			mTvTitle.setText(R.string.header_title_scan);
			mMenu = new SlidingMenuCustom(this, this);
			// Configure orientation for displaying Sliding Menu and Camera
			int display_mode = getResources().getConfiguration().orientation;
			if (display_mode == 1) {
				mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset_land);
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			mFrameCamera = (FrameLayout) findViewById(R.id.activity_scan_camera);
			mDataHandler = new DatabaseHandler(this);

			if (!isCameraAvailable()) {
				// Cancel request if there is no rear-facing camera.
				cancelRequest();
				return;
			}

			mAutoFocusHandler = new Handler();
			// Create and configure the ImageScanner;
			setupScanner();
			// Show warning dialog for Invalid URL
			createInvalidURLDialog(ScanActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setupScanner() {
		try {
			mScanner = new ImageScanner();
			mScanner.setConfig(0, Config.X_DENSITY, 3);
			mScanner.setConfig(0, Config.Y_DENSITY, 3);

			// int[] symbols = new int[] { Symbol.QRCODE };
			// if (symbols != null) {
			// mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
			// for (int symbol : symbols) {
			// mScanner.setConfig(symbol, Config.ENABLE, 1);
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
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
			/* create layout for SurfaceView here */
			mPreview = new CameraPreviewNew(this, this, autoFocusCB);
			mFrameCamera.addView(mPreview);

			mPreview.setCamera(mCamera);

			mPreviewing = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			// Because the Camera object is a shared resource, it's very
			// important to release it when the activity is paused.
			if (mCamera != null) {
				mPreview.setCamera(null);
				mCamera.cancelAutoFocus();
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				// this line make difference
				mPreview.getHolder().removeCallback(mPreview);
				mCamera.release();
				mCamera = null;

				// remove layout Camera
				mFrameCamera.removeView(mPreview);
				mPreview = null;

				mPreviewing = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (mAppPreferences != null) {
				mAppPreferences = null;
			}
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.setPreviewCallback(null);
				mCamera.release();
				mCamera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	public void cancelRequest() {
		try {
			Toast.makeText(
					ScanActivity.this,
					ScanActivity.this
							.getString(R.string.activity_scan_camera_unavailable),
					Toast.LENGTH_LONG).show();
			// Intent dataIntent = new Intent();
			// dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
			// setResult(Activity.RESULT_CANCELED, dataIntent);
			// finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onPreviewFrame(byte[] data, Camera camera) {
		try {
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
						// Check whether to play sound or not
						if (mAppPreferences.isSound()) {
							mAudio.setStreamVolume(
									AudioManager.STREAM_MUSIC,
									mAudio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
									0);
							playSound();
						}
						// Stop scanning
						mCamera.cancelAutoFocus();
						mCamera.setPreviewCallback(null);

						if (mAppPreferences.getProfileUrl().equalsIgnoreCase(
								"-1")) {
							// There is no URL profile format
							continueScan(symData);
						} else {
							// Check whether URL follow URL profile format or
							// not like "cptr.it/?var={variable}&id=test"
							String[] domain = symData.split("/");
							if (domain[0].contains(".")
									&& symData.contains("?var=")
									&& symData.contains("&id=test")) {
								continueScan(symData);
							} else if (!alertDialog.isShowing()) {
								alertDialog.show();
							}
						}
					}
				}
				// Turn on Camera Preview
				mCamera.startPreview();
				mPreviewing = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void continueScan(final String symData) {
		Format formatter = new SimpleDateFormat("EEEE, MMMM dd yyyy", Locale.US);

		String date = formatter.format(new Date());
		mDataHandler.addQRCode(new QRCode(date, symData));

		// Get the QR Code after scanning and put it to
		// Browser
		// for
		// searching on WebSite

		if (mAppPreferences.isOpenUrl()) {
			Intent dataIntent = new Intent(ScanActivity.this,
					BrowserActivity.class);
			dataIntent.putExtra(StringExtraUtils.KEY_SCAN_RESULT, symData);
			startActivity(dataIntent);
		} else {
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

							Intent dataIntent = new Intent(ScanActivity.this,
									BrowserActivity.class);
							dataIntent.putExtra(
									StringExtraUtils.KEY_SCAN_RESULT, symData);
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
	}

	/* INVALID URL DIALOG */

	public void createInvalidURLDialog(Context context) {
		try {
			alertDialogBuilder = new AlertDialog.Builder(context);

			// set title
			alertDialogBuilder.setTitle(context.getResources().getString(
					R.string.activity_scan_invalid_url_title));

			// set dialog message
			alertDialogBuilder
					.setMessage(
							context.getResources().getString(
									R.string.activity_scan_invalid_url_message))
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									startPreviewAgain();
								}
							}); // create alert dialog
			alertDialog = alertDialogBuilder.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPreviewAgain() {
		try {
			// Turn on Camera Preview
			mCamera.startPreview();
			mPreviewing = true;
			// Start scanning again
			mCamera.setPreviewCallback(ScanActivity.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void onClick_Menu(View view) {
		try {
			if (mMenu == null) {
				mMenu = new SlidingMenuCustom(this, this);
			}
			mMenu.toggle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onScannerClickListener() {
		try {
			mMenu.toggle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHistoryClickListener() {
		try {
			startActivity(new Intent(ScanActivity.this, HistoryActivity.class));
			finish();
			overridePendingTransition(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAboutClickListener() {
		try {
			startActivity(new Intent(this, AboutActivity.class));
			finish();
			overridePendingTransition(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSettingClickListener() {
		try {
			startActivity(new Intent(ScanActivity.this, SettingActivity.class));
			finish();
			overridePendingTransition(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
							getString(R.string.press_exit), Toast.LENGTH_SHORT)
							.show();
					lastPressedTime = event.getEventTime();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset_land);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			mMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		}
	}
}
