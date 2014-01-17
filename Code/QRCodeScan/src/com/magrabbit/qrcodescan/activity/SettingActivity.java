package com.magrabbit.qrcodescan.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.control.SwitchView;
import com.magrabbit.qrcodescan.control.SwitchView.OnSwitchChangeListener;
import com.magrabbit.qrcodescan.customview.DialogPickTime;
import com.magrabbit.qrcodescan.customview.DialogPickTime.ProcessDialogPickTime;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.AppPreferences;
import com.magrabbit.qrcodescan.utils.SocialUtil;

/**
 * @author vule
 *
 */
public class SettingActivity extends Activity implements
		MenuSlidingClickListener, OnSwitchChangeListener, OnClickListener {

	private SlidingMenuCustom mMenu;
	private SwitchView mSwitchViewSound;
	private SwitchView mSwitchViewOpenUrl;
	private AppPreferences mAppPreferences;
	private TextView mTvTime;
	private TextView mTvTitle;
	private ImageButton mBtRight;
	private RelativeLayout mRlShareFacebook;
	private RelativeLayout mRlShareSMS;
	private RelativeLayout mRlShareMail;
	private RelativeLayout mRlShareTwitter;
	private Facebook mFacebook = new Facebook(SocialUtil.FACEBOOK_APPID);
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mTvTitle = (TextView) findViewById(R.id.header_tv_title);
		mBtRight = (ImageButton) findViewById(R.id.header_bt_right);
		mTvTitle.setText(R.string.header_title_setting);
		mBtRight.setVisibility(View.GONE);

		mAppPreferences = new AppPreferences(this);
		mMenu = new SlidingMenuCustom(this, this);
		mMenu.setTouchModeAboveMargin();
		mSwitchViewSound = (SwitchView) findViewById(R.id.activity_settings_sv_sound);
		mSwitchViewOpenUrl = (SwitchView) findViewById(R.id.activity_settings_sv_open_url);
		mTvTime = (TextView) findViewById(R.id.activity_setting_tv_time);
		mRlShareFacebook = (RelativeLayout) findViewById(R.id.setting_ll_social_facebook);
		mRlShareSMS = (RelativeLayout) findViewById(R.id.setting_ll_social_message);
		mRlShareMail = (RelativeLayout) findViewById(R.id.setting_ll_social_mail);
		mRlShareTwitter = (RelativeLayout) findViewById(R.id.setting_ll_social_twitter);
		mRlShareSMS.setOnClickListener(this);
		mRlShareMail.setOnClickListener(this);
		mRlShareTwitter.setOnClickListener(this);
		mRlShareFacebook.setOnClickListener(this);

		mSwitchViewSound.setOnSwitchChangeListener(this);
		mSwitchViewOpenUrl.setOnSwitchChangeListener(this);

		mSwitchViewSound.setSwitchOn(mAppPreferences.isSound());
		mSwitchViewOpenUrl.setSwitchOn(mAppPreferences.isOpenUrl());
		if (mAppPreferences.getCloseUrlTime() == -1) {
			mTvTime.setText("Nerver");
		} else {
			mTvTime.setText(mAppPreferences.getCloseUrlTime() + " seconds");
		}
	}

	public void onClick_Menu(View view) {
		if (mMenu == null) {
			mMenu = new SlidingMenuCustom(this, this);
		}
		mMenu.toggle();
	}

	@Override
	public void onScannerClickListener() {
		startActivity(new Intent(this, ScanActivity.class));
		finish();
		overridePendingTransition(0,0);
	}

	@Override
	public void onHistoryClickListener() {
		startActivity(new Intent(this, HistoryActivity.class));
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

	}

	@Override
	public void onSwitchChanged(View view, boolean isOn) {

		switch (view.getId()) {
		case R.id.activity_settings_sv_sound:
			mAppPreferences.setSound(isOn);
			break;
		case R.id.activity_settings_sv_open_url:
			mAppPreferences.setOpenUrl(isOn);
			break;
		default:
			break;
		}
	}

	public void onClick_AutoClose(View v) {
		DialogPickTime d = new DialogPickTime(this,
				new ProcessDialogPickTime() {

					@Override
					public void click_Ok(int value) {
						mAppPreferences.setCloseUrl(value);
						if (mAppPreferences.getCloseUrlTime() == -1) {
							mTvTime.setText("Nerver");
						} else {
							mTvTime.setText(mAppPreferences.getCloseUrlTime()
									+ " seconds");
						}
					}

					@Override
					public void click_Cancel() {

					}
				}, mAppPreferences.getCloseUrlTime());
		d.show();
	}

	@Override
	public void onClick(View v) {
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
			Toast.makeText(this, "Share via Twitter", Toast.LENGTH_SHORT)
					.show();
			break;
		}

		case R.id.setting_ll_social_facebook: {
			loginToFacebook();
			break;
		}

		default:
			break;
		}
	}

	public void loginToFacebook() {
		
		try  {

		      PackageInfo info = getPackageManager().
		           getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);

		      for (Signature signature : info.signatures) {

		          MessageDigest md = MessageDigest.getInstance("SHA");
		          md.update(signature.toByteArray());
		          Log.d("====Hash Key===",Base64.encodeToString(md.digest(), 
		                   Base64.DEFAULT));
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

	public void getAccessToken() {
		String access_token = mFacebook.getAccessToken();
		Toast.makeText(getApplicationContext(),
				"Access Token: " + access_token, Toast.LENGTH_LONG).show();
	}

	public void postToWall() {
		Bundle parameters = new Bundle();
		parameters.putString("link", "http://toitesthuthoi.com");
		mFacebook.dialog(this, "feed", parameters, new DialogListener() {

			@Override
			public void onFacebookError(FacebookError e) {
			}

			@Override
			public void onError(DialogError e) {
			}

			@Override
			public void onComplete(Bundle values) {
				Toast.makeText(SettingActivity.this,
						"Commment has been posted in your wall!",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
			}
		});
	}

	protected void sendSMS() {
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.putExtra("sms_body", "Please follow this link...");
		smsIntent.setType("vnd.android-dir/mms-sms");

		try {
			startActivity(smsIntent);
			// finish();
		} catch (Exception e) {
			Toast.makeText(this, "Please insert your simcard.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	protected void sendMail() {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Follow this link");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "QRcode link http://qrcode.com");
		try {
			startActivity(Intent.createChooser(emailIntent, "Choose an Email client:"));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

}
