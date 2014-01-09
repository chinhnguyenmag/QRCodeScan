package com.magrabbit.qrcodescan.customview;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;

/**
 * @author Hung Hoang This class init Sliding Menu.
 */
public class SlidingMenuCustom {
	private SlidingMenu menu = null;
	private Context mContext;
	private Button mBtScanner;
	private Button mBtHistory;
	private Button mBtSetting;
	private Button mBtAbout;

	/**
	 * @param context
	 * @param listenner
	 */
	public SlidingMenuCustom(Context context,
			final MenuSlidingClickListener listenner) {
		menu = new SlidingMenu(mContext);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.anim.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity((Activity) mContext, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.activity_sliding);
		menu.setSlidingEnabled(true);
		View view = menu.getRootView();
		mBtAbout = (Button) view.findViewById(R.id.activity_sliding_bt_about);
		mBtHistory = (Button) view
				.findViewById(R.id.activity_sliding_bt_history);
		mBtScanner = (Button) view
				.findViewById(R.id.activity_sliding_bt_scanner);
		mBtSetting = (Button) view
				.findViewById(R.id.activity_sliding_bt_setting);

		mBtAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menu.toggle();
				listenner.onAboutClickListener();
			}
		});

		mBtHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menu.toggle();
				listenner.onHistoryClickListener();
			}
		});

		mBtScanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menu.toggle();
				listenner.onScannerClickListener();
			}
		});

		mBtSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				menu.toggle();
				listenner.onSettingClickListener();
			}
		});
	}
}
