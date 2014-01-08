package com.magrabbit.qrcodescan.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.magrabbit.qrcodescan.R;

public class MainActivity extends Activity {
	SlidingMenu menu = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMenu();
	}
	public void onClick_Menu(View view) {
		if (menu == null) {
			initMenu();
		}
		menu.setMode(SlidingMenu.LEFT);
		menu.toggle();
	}

	private void initMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setFadeDegree(0.5f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.activity_sliding);
		View view = menu.getRootView();
		menu.setBehindOffset(200);
	}
}
