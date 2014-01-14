package com.magrabbit.qrcodescan.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.adapter.HistoryAdapter;
import com.magrabbit.qrcodescan.control.DatabaseHandler;
import com.magrabbit.qrcodescan.customview.SlidingMenuCustom;
import com.magrabbit.qrcodescan.listener.MenuSlidingClickListener;
import com.magrabbit.qrcodescan.model.HistoryItem;
import com.magrabbit.qrcodescan.model.Item;
import com.magrabbit.qrcodescan.model.QRCode;
import com.magrabbit.qrcodescan.utils.StringExtraUtils;

public class HistoryActivity extends Activity implements
		MenuSlidingClickListener {

	private HistoryAdapter mAdapter;
	private ArrayList<Item> items = new ArrayList<Item>();
	private SlidingMenuCustom mMenu;
	private SwipeListView mSwipeListView;
	private DatabaseHandler mDataHandler;
	private List<QRCode> mListQRCodes;

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
	protected void onCreate(Bundle savedInstanceState) {
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
		items.add(new HistorySectionItem(mListQRCodes.get(0).getDate()));
		items.add(new HistoryItem(mListQRCodes.get(0).getUrl()));

		for (int i = 1; i < mListQRCodes.size(); i++) {
			if (!mListQRCodes.get(i).getDate()
					.equals(mListQRCodes.get(i - 1).getDate())) {
				// section
				items.add(new HistorySectionItem(mListQRCodes.get(i).getDate()));
				items.add(new HistoryItem(mListQRCodes.get(i).getUrl()));
			} else {
				// item
				items.add(new HistoryItem(mListQRCodes.get(i).getUrl()));
			}
		}
		
		mAdapter = new HistoryAdapter(this, items);
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
}
