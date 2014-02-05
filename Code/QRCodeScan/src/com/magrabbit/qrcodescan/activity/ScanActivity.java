package com.magrabbit.qrcodescan.activity;

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
import android.hardware.Camera;
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
	private FrameLayout mFrameCamera;
	// Application Preference
	private AppPreferences mPreference;
	// For Sliding Menu
	private SlidingMenuCustom mSlidingMenu;
	// Save scanned QRCode into local database by SQLite
	private DatabaseHandler mDataHandler;
	private TextView mTvTitle;
	private AlertDialog.Builder alertDialogBuilder;
	private AlertDialog alertDialog;
	private long lastPressedTime;
	private static final int PERIOD = 2000;

	static {
		System.loadLibrary("iconv");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		try {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			mTvTitle = (TextView) findViewById(R.id.header_tv_title);
			mTvTitle.setText(R.string.header_title_scan);
			mSlidingMenu = new SlidingMenuCustom(this, this);

			mPreference = new AppPreferences(ScanActivity.this);
			mFrameCamera = (FrameLayout) findViewById(R.id.activity_scan_camera);
			mDataHandler = new DatabaseHandler(this);

			if (!isCameraAvailable()) {
				// Cancel request if there is no rear-facing camera.
				cancelRequest();
				return;
			}

			mAutoFocusHandler = new Handler();
			// mCamera = getCameraInstance();
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

			int[] symbols = new int[] { Symbol.QRCODE };
			if (symbols != null) {
				mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
				for (int symbol : symbols) {
					mScanner.setConfig(symbol, Config.ENABLE, 1);
				}
			}
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
			if (mPreference != null) {
				mPreference = null;
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
			Intent dataIntent = new Intent();
			dataIntent.putExtra(ERROR_INFO, "Camera unavailable");
			setResult(Activity.RESULT_CANCELED, dataIntent);
//			finish();
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
						if (mPreference.isSound()) {
							playSound();
						}
						// Stop scanning
						mCamera.cancelAutoFocus();
						mCamera.setPreviewCallback(null);

						// if (symData.contains("cptr.it/?var=")
						// && symData.contains("&id=")) {
						// Save into Database
						Format formatter = new SimpleDateFormat(
								"EEEE, MMMM dd yyyy", Locale.US);

						// DateFormat formatter =
						// DateFormat.getDateInstance(
						// DateFormat.LONG, Locale.US);
						String date = formatter.format(new Date());
						mDataHandler.addQRCode(new QRCode(date, symData));

						// Get the QR Code after scanning and put it to
						// Browser
						// for
						// searching on WebSite

						if (mPreference.isOpenUrl()) {
							Intent dataIntent = new Intent(ScanActivity.this,
									BrowserActivity.class);
							dataIntent.putExtra(
									StringExtraUtils.KEY_SCAN_RESULT, symData);
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

						// } else {
						// // show it
						// if (!alertDialog.isShowing()) {
						// alertDialog.show();
						// }
						// }
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
			if (mSlidingMenu == null) {
				mSlidingMenu = new SlidingMenuCustom(this, this);
			}
			mSlidingMenu.toggle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onScannerClickListener() {
		try {
			mSlidingMenu.toggle();
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
}
