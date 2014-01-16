package com.magrabbit.qrcodescan.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.adapter.HistoryAdapter;
import com.magrabbit.qrcodescan.adapter.HistoryAdapter.HistoryAdapter_Process;
import com.magrabbit.qrcodescan.control.DatabaseHandler;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.HistoryItem;
import com.magrabbit.qrcodescan.model.HistorySectionItem;
import com.magrabbit.qrcodescan.model.Item;
import com.magrabbit.qrcodescan.model.QRCode;
import com.magrabbit.qrcodescan.utils.SocialUtil;
import com.magrabbit.qrcodescan.utils.StringExtraUtils;

public class HistoryActivity extends ParentActivity implements
		MenuSlidingClickListener {

	private HistoryAdapter mAdapter;
	private ArrayList<Item> items = new ArrayList<Item>();
	private SlidingMenuCustom mMenu;
	private SwipeListView mSwipeListView;
	private DatabaseHandler mDataHandler;
	private List<QRCode> mListQRCodes;
	private int mSectionNumber = 0;
	private Facebook mFacebook = new Facebook(SocialUtil.FACEBOOK_APPID);
	private SharedPreferences mSharedPreferences;

	// =============================================================
	private int swipeMode = SwipeListView.SWIPE_MODE_BOTH;
	private boolean swipeOpenOnLongPress = true;
	private boolean swipeCloseAllItemsWhenMoveList = true;
	private long swipeAnimationTime = 0;
	private float swipeOffsetLeft = 250;
	private float swipeOffsetRight = 100;
	private int swipeActionLeft = SwipeListView.SWIPE_ACTION_REVEAL;
	private int swipeActionRight = SwipeListView.SWIPE_ACTION_REVEAL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		// inflate layout for list view
		mSwipeListView = (SwipeListView) findViewById(R.id.activity_history_lv);
		mMenu = new SlidingMenuCustom(this, this);
		mMenu.setTouchModeAboveMargin();

		// Get QRCode from Database to inflate into list view
		mDataHandler = new DatabaseHandler(this);
		mListQRCodes = new ArrayList<QRCode>();
		mListQRCodes.addAll(mDataHandler.getAllQRCodes());
		// add the first section and item into list view
		if (mListQRCodes.size() != 0) {
			items.add(new HistorySectionItem(mListQRCodes.get(0).getDate()));
			items.add(new HistoryItem(mListQRCodes.get(0).getUrl()));

			for (int i = 1; i < mListQRCodes.size(); i++) {
				if (!mListQRCodes.get(i).getDate()
						.equals(mListQRCodes.get(i - 1).getDate())) {
					// section
					items.add(new HistorySectionItem(mListQRCodes.get(i)
							.getDate()));
					items.add(new HistoryItem(mListQRCodes.get(i).getUrl()));
				} else {
					// item
					items.add(new HistoryItem(mListQRCodes.get(i).getUrl()));
				}
			}
		}

		mAdapter = new HistoryAdapter(this, items,
				new HistoryAdapter_Process() {

					@Override
					public void delete_item(int position) {
						// delete from database
						mDataHandler.deleteQRCode(mListQRCodes.get(position));
						items.remove(position);
						mAdapter.notifyDataSetChanged();
						mSwipeListView.setAdapter(mAdapter);
					}

					@Override
					public void click_evernote(int position) {
						addEverNote(null);
					}

					@Override
					public void click_facebook(int position) {
						loginToFacebook(mListQRCodes.get(position).getUrl()
								.trim());
					}

					@Override
					public void click_twitter(int position) {
						Toast.makeText(HistoryActivity.this,
								"Share via Twitter", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void click_sms(int position) {
						sendSMS(mListQRCodes.get(position).getUrl().trim());
					}

					@Override
					public void click_email(int position) {
						sendMail(mListQRCodes.get(position).getUrl().trim());
					}

				});
		mSwipeListView
				.setSwipeListViewListener(new BaseSwipeListViewListener() {

					@Override
					public void onStartOpen(int position, int action,
							boolean right) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartClose(int position, boolean right) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onOpened(int position, boolean toRight) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMove(int position, float x) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onListChanged() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLastListItem() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFirstListItem() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDismiss(int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							items.remove(position);
						}
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onClosed(int position, boolean fromRight) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onClickFrontView(int position) {
						if (!items.get(position).isSection()) {
							// Add code to process
							HistoryItem item = (HistoryItem) items
									.get(position);
							Intent dataIntent = new Intent(
									HistoryActivity.this, BrowserActivity.class);
							dataIntent.putExtra(
									StringExtraUtils.KEY_SCAN_RESULT,
									item.getTitle());
							startActivity(dataIntent);
						}
					}

					@Override
					public void onClickBackView(int position) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChoiceStarted() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChoiceEnded() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChoiceChanged(int position, boolean selected) {
						// TODO Auto-generated method stub

					}

				});

		// Set Adapter for List View
		mSwipeListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		reload();

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
	}

	@Override
	public void onHistoryClickListener() {
		mMenu.toggle();

	}

	@Override
	public void onAboutClickListener() {
		startActivity(new Intent(this, AboutActivity.class));
		finish();

	}

	@Override
	public void onSettingClickListener() {
		startActivity(new Intent(this, SettingActivity.class));
		overridePendingTransition(0, 0);
		finish();
	}

	public void onClick_ClearAll(View v) {
		// delete all QR Code from Database
		for (QRCode code : mListQRCodes) {
			mDataHandler.deleteQRCode(code);
		}
		items.clear();
		mAdapter.notifyDataSetChanged();
		mSwipeListView.setAdapter(mAdapter);

	}

	private void reload() {
		mSwipeListView.setSwipeMode(swipeMode);
		mSwipeListView.setSwipeActionLeft(swipeActionLeft);
		mSwipeListView.setSwipeActionRight(swipeActionRight);
		mSwipeListView.setOffsetLeft(convertDpToPixel(swipeOffsetLeft));
		mSwipeListView.setOffsetRight(convertDpToPixel(swipeOffsetRight));
		mSwipeListView.setAnimationTime(swipeAnimationTime);
		mSwipeListView.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	/**
	 * @param v
	 * @Description Processing login and adding a new EverNote
	 */
	public void addEverNote(View v) {
		mEvernoteSession.authenticate(HistoryActivity.this);
	}

	/**
	 * Called when the control returns from an activity that we launched.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mFacebook.authorizeCallback(requestCode, resultCode, data);
		switch (requestCode) {
		// Add a new EverNote when OAuth activity returns result
		case EvernoteSession.REQUEST_CODE_OAUTH:
			if (resultCode == Activity.RESULT_OK) {
				startActivity(new Intent(HistoryActivity.this,
						CreateEverNote.class));
			}
			break;
		}
	}

	public void loginToFacebook(final String link) {

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
			postToWall(link);
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

							postToWall(link);
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

	public void postToWall(String link) {
		Bundle parameters = new Bundle();
		parameters.putString("link", link);
		mFacebook.dialog(this, "feed", parameters, new DialogListener() {

			@Override
			public void onFacebookError(FacebookError e) {
			}

			@Override
			public void onError(DialogError e) {
			}

			@Override
			public void onComplete(Bundle values) {
				Toast.makeText(HistoryActivity.this,
						"Commment has been posted in your wall!",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
			}
		});
	}

	protected void sendSMS(String link) {
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.putExtra("sms_body", link);
		smsIntent.setType("vnd.android-dir/mms-sms");

		try {
			startActivity(smsIntent);
			// finish();
		} catch (Exception e) {
			Toast.makeText(this, "Please insert your simcard.",
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void sendMail(String link) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Follow this link");
		emailIntent.putExtra(Intent.EXTRA_TEXT, link);
		try {
			startActivity(Intent.createChooser(emailIntent,
					"Choose an Email client:"));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "There are no email clients installed.",
					Toast.LENGTH_SHORT).show();
		}
	}
}
