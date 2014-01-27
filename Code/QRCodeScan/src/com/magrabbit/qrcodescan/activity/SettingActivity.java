package com.magrabbit.qrcodescan.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.customview.DialogPickTime;
import com.magrabbit.qrcodescan.customview.DialogPickTime.ProcessDialogPickTime;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.AppPreferences;
import com.magrabbit.qrcodescan.utils.SocialUtil;
import com.magrabbit.qrcodescan.utils.Utils;

/**
 * @author vule
 * @description: This class use to setting application
 * 
 */
public class SettingActivity extends BaseActivity implements
		MenuSlidingClickListener, OnClickListener {

	private SlidingMenuCustom mMenu;
	private ToggleButton mSwitchViewSound;
	private ToggleButton mSwitchViewOpenUrl;
	private AppPreferences mAppPreferences;
	private TextView mTvTime;
	private TextView mTvTitle;
	private RelativeLayout mRlShareFacebook;
	private RelativeLayout mRlShareSMS;
	private RelativeLayout mRlShareMail;
	private RelativeLayout mRlShareTwitter;
	private Facebook mFacebook = new Facebook(SocialUtil.FACEBOOK_APPID);
	private SharedPreferences mSharedPreferences;
	private long lastPressedTime;
	private static final int PERIOD = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		try {
			mTvTitle = (TextView) findViewById(R.id.header_tv_title);
			mTvTitle.setText(R.string.header_title_setting);

			mAppPreferences = new AppPreferences(this);
			mMenu = new SlidingMenuCustom(this, this);
			mMenu.setTouchModeAboveMargin();
			mSwitchViewSound = (ToggleButton) findViewById(R.id.activity_settings_sv_sound);
			mSwitchViewOpenUrl = (ToggleButton) findViewById(R.id.activity_settings_sv_open_url);
			mTvTime = (TextView) findViewById(R.id.activity_setting_tv_time);
			mRlShareFacebook = (RelativeLayout) findViewById(R.id.setting_ll_social_facebook);
			mRlShareSMS = (RelativeLayout) findViewById(R.id.setting_ll_social_message);
			mRlShareMail = (RelativeLayout) findViewById(R.id.setting_ll_social_mail);
			mRlShareTwitter = (RelativeLayout) findViewById(R.id.setting_ll_social_twitter);
			mRlShareSMS.setOnClickListener(this);
			mRlShareMail.setOnClickListener(this);
			mRlShareTwitter.setOnClickListener(this);
			mRlShareFacebook.setOnClickListener(this);

			mSwitchViewSound.setChecked(mAppPreferences.isSound());
			mSwitchViewOpenUrl.setChecked(mAppPreferences.isOpenUrl());
			mSwitchViewSound
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean isOn) {
							mAppPreferences.setSound(isOn);
						}
					});

			mSwitchViewOpenUrl
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean isOn) {
							mAppPreferences.setOpenUrl(isOn);
						}
					});

			if (mAppPreferences.getCloseUrlTime() == -1) {
				mTvTime.setText("Nerver");
			} else {
				mTvTime.setText(mAppPreferences.getCloseUrlTime() + " seconds");
			}

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
			startActivity(new Intent(this, ScanActivity.class));
			finish();
			overridePendingTransition(0, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHistoryClickListener() {
		try {
			startActivity(new Intent(this, HistoryActivity.class));
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

	}

	public void onClick_AutoClose(View v) {
		try {
			DialogPickTime d = new DialogPickTime(this,
					new ProcessDialogPickTime() {

						@Override
						public void click_Ok(int value) {
							mAppPreferences.setCloseUrl(value);
							if (mAppPreferences.getCloseUrlTime() == -1) {
								mTvTime.setText("Nerver");
							} else {
								mTvTime.setText(mAppPreferences
										.getCloseUrlTime() + " seconds");
							}
						}

						@Override
						public void click_Cancel() {

						}
					}, mAppPreferences.getCloseUrlTime());
			d.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.setting_ll_social_message: {
				sendSMS();
				break;
			}

			case R.id.setting_ll_social_mail: {
				sendMail();

				break;
			}

			case R.id.setting_ll_social_twitter: {
				if (Utils.isNetworkConnected(this)) {
					Intent intent = new Intent(SettingActivity.this,
							TwitterLoginActivity.class);
					startActivity(intent);
				} else {
					showToastMessage(getString(R.string.mess_error_network));
				}
				break;
			}

			case R.id.setting_ll_social_facebook: {
				if (Utils.isNetworkConnected(this)) {
					loginToFacebook();
				} else {
					showToastMessage(getString(R.string.mess_error_network));
				}
				break;
			}

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method use to login facebook
	 */
	public void loginToFacebook() {

		try {

			PackageInfo info = getPackageManager().getPackageInfo(
					this.getPackageName(), PackageManager.GET_SIGNATURES);

			for (Signature signature : info.signatures) {

				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("====Hash Key===",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}

		} catch (NameNotFoundException e) {

			e.printStackTrace();

		} catch (NoSuchAlgorithmException ex) {

			ex.printStackTrace();

		}
		mSharedPreferences = getPreferences(MODE_PRIVATE);
		String access_token = mSharedPreferences
				.getString("access_token", null);
		long expires = mSharedPreferences.getLong("access_expires", 0);
		if (access_token != null) {
			mFacebook.setAccessToken(access_token);
			postToWall();
		}

		if (expires != 0) {
			mFacebook.setAccessExpires(expires);
		}

		if (!mFacebook.isSessionValid()) {
			mFacebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

						@Override
						public void onFacebookError(FacebookError e) {
						}

						@Override
						public void onError(DialogError e) {
						}

						@Override
						public void onComplete(Bundle values) {
							SharedPreferences.Editor editor = mSharedPreferences
									.edit();
							editor.putString("access_token",
									mFacebook.getAccessToken());
							editor.putLong("access_expires",
									mFacebook.getAccessExpires());
							editor.commit();

							postToWall();
						}

						@Override
						public void onCancel() {
						}
					});
		}
	}

	/**
	 * This method use to get access token from facebook
	 */
	public void getAccessToken() {
		try {
			String access_token = mFacebook.getAccessToken();
			Toast.makeText(getApplicationContext(),
					"Access Token: " + access_token, Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method use to post to wall on facebook
	 */
	public void postToWall() {
		try {
			Bundle parameters = new Bundle();

			Uri captixUrl = Uri
					.parse("http://www7.44doors.com/dl.aspx?cid=2444&pv_url=http://cptr.it/captixscan?2444_rm_id=100.3781911.7");

			parameters
					.putString(
							"link",
							Html.fromHtml(getString(R.string.content_to_share_social_media)
									+ "<a href=\""
									+ captixUrl
									+ "\"> cptr.it/captixscan</a>")
									+ "");

			mFacebook.dialog(this, "feed", parameters, new DialogListener() {

				@Override
				public void onFacebookError(FacebookError e) {
				}

				@Override
				public void onError(DialogError e) {
					e.printStackTrace();
				}

				@Override
				public void onComplete(Bundle values) {
					Toast.makeText(SettingActivity.this,
							getString(R.string.mess_post_success),
							Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onCancel() {
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method use to call intent sms system to send
	 */
	protected void sendSMS() {
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent
				.putExtra(
						"sms_body",
						getString(R.string.content_to_share_social_media)
								+ " http://www7.44doors.com/dl.aspx?cid=2444&pv_url=http://cptr.it/captixscan?2444_rm_id=100.3781911.7");
		smsIntent.setType("vnd.android-dir/mms-sms");

		try {
			startActivity(smsIntent);
			// finish();
		} catch (Exception e) {
			Toast.makeText(this, "Please insert your simcard.",
					Toast.LENGTH_SHORT).show();
		}
	}

	// @Override
	// public void onBackPressed() {
	// if (count == 1) {
	// count = 0;
	// finish();
	// } else {
	// Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT)
	// .show();
	// count++;
	// }
	// return;
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		try {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				switch (event.getAction()) {
				case KeyEvent.ACTION_DOWN:
					if (event.getDownTime() - lastPressedTime < PERIOD) {
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								getString(R.string.press_exit),
								Toast.LENGTH_SHORT).show();
						lastPressedTime = event.getEventTime();
					}
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			mFacebook.authorizeCallback(requestCode, resultCode, data);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method use to call intent email system to send
	 */
	protected void sendMail() {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		// emailIntent.setType("message/rfc822");
		emailIntent.setType("text/html");
		emailIntent
				.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));

		Uri captixUrl = Uri
				.parse("http://www7.44doors.com/dl.aspx?cid=2444&pv_url=http://cptr.it/captixscan?2444_rm_id=100.3781911.7");

		// ============1============
		emailIntent.putExtra(Intent.EXTRA_TEXT,
				getString(R.string.content_to_share_social_media));

		// ============2============
		emailIntent.putExtra(
				android.content.Intent.EXTRA_TEXT,
				Html.fromHtml(getString(R.string.content_to_share_social_media)
						+ "<a href=\"" + captixUrl
						+ "\"> cptr.it/captixscan</a>"));
		try {
			startActivity(Intent.createChooser(emailIntent,
					"Choose an Email client:"));
		} catch (Exception ex) {
			Toast.makeText(this, "There are no email clients installed.",
					Toast.LENGTH_SHORT).show();
		}
	}

}
