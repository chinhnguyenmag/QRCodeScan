package com.magrabbit.qrcodescan.activity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.adapter.HistoryAdapter;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.HistoryItem;
import com.magrabbit.qrcodescan.model.HistorySectionItem;
import com.magrabbit.qrcodescan.model.Item;

public class HistoryActivity extends Activity implements
		MenuSlidingClickListener {

	private ListView mLvHistory;
	private HistoryAdapter mAdapter;
	private ArrayList<Item> items = new ArrayList<Item>();
	private SlidingMenuCustom mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		// inflate layout for list view
		mLvHistory = (ListView) findViewById(R.id.activity_history_lv);
		mMenu = new SlidingMenuCustom(this, this);

		long time = System.currentTimeMillis();

		Format formatter = new SimpleDateFormat("EEE, MMM dd yyyy");
		String date = formatter.format(new Date());

		items.add(new HistorySectionItem(date));
		items.add(new HistoryItem("http://www.44doors.com"));
		items.add(new HistoryItem("http://www.wikipedia.com"));
		items.add(new HistorySectionItem(date));
		items.add(new HistoryItem("http://www.google.com"));
		items.add(new HistoryItem("http://www.44doors.com"));
		items.add(new HistoryItem("http://www.44doors.com"));

		mAdapter = new HistoryAdapter(this, items);
		mLvHistory.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		// Process click event on each item of list
		mLvHistory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				if (!items.get(position).isSection()) {
					// Add code to process
					startActivity(new Intent(HistoryActivity.this,
							BrowserActivity.class));
				}

			}
		});
	}

	public void onClick_Menu(View view) {
		if (mMenu == null) {
			mMenu = new SlidingMenuCustom(this, this);
		}
		mMenu.toggle();
	}

	@Override
	public void onScannerClickListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHistoryClickListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAboutClickListener() {
		startActivity(new Intent(this, AboutActivity.class));
		finish();

	}

	@Override
	public void onSettingClickListener() {
		startActivity(new Intent(this, SettingActivity.class));
		finish();

	}
}
