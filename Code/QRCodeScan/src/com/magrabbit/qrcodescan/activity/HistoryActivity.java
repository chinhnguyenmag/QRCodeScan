package com.magrabbit.qrcodescan.activity;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import model.HistoryItem;
import model.HistorySectionItem;
import model.Item;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.adapter.HistoryAdapter;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;

public class HistoryActivity extends Activity implements MenuSlidingClickListener {

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
		items.add(new HistoryItem("Item 1", "This is item 1.1"));
		items.add(new HistoryItem("Item 2", "This is item 1.2"));
		items.add(new HistoryItem("Item 3", "This is item 1.3"));

		items.add(new HistorySectionItem("Category 2"));
		items.add(new HistoryItem("Item 4", "This is item 2.1"));
		items.add(new HistoryItem("Item 5", "This is item 2.2"));
		items.add(new HistoryItem("Item 6", "This is item 2.3"));
		items.add(new HistoryItem("Item 7", "This is item 2.4"));

		items.add(new HistorySectionItem("Category 3"));
		items.add(new HistoryItem("Item 8", "This is item 3.1"));
		items.add(new HistoryItem("Item 9", "This is item 3.2"));
		items.add(new HistoryItem("Item 10", "This is item 3.3"));
		items.add(new HistoryItem("Item 11", "This is item 3.4"));
		items.add(new HistoryItem("Item 12", "This is item 3.5"));

		mAdapter = new HistoryAdapter(this, items);
		mLvHistory.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSettingClickListener() {
		// TODO Auto-generated method stub
		
	}
}
