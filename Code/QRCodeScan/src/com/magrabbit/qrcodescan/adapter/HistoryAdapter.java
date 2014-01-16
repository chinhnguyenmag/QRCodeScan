package com.magrabbit.qrcodescan.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.model.HistoryItem;
import com.magrabbit.qrcodescan.model.HistorySectionItem;
import com.magrabbit.qrcodescan.model.Item;

public class HistoryAdapter extends ArrayAdapter<Item> {

	private Context context;
	private List<Item> items;
	private LayoutInflater vi;

	public HistoryAdapter(Context context, ArrayList<Item> items) {
		super(context, 0, items);
		this.context = context;
		this.items = new ArrayList<Item>();
		this.items.addAll(items);
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try {
			View v = convertView;
			ViewHolder holder;
			final Item i = items.get(position);
			if (i != null) {
				if (i.isSection()) {
					HistorySectionItem si = (HistorySectionItem) i;
					// inflate view
					v = vi.inflate(R.layout.list_item_section, null);

					v.setOnClickListener(null);
					v.setOnLongClickListener(null);
					v.setLongClickable(false);

					final TextView sectionView = (TextView) v
							.findViewById(R.id.list_item_section_text);
					sectionView.setText(si.getTitle());
				} else {
					holder = new ViewHolder();
					HistoryItem ei = (HistoryItem) i;
					v = vi.inflate(R.layout.list_item_entry, null);
					holder.mBtnDelete = (Button) v
							.findViewById(R.id.list_item_entry_btn_delete);
					holder.mBtnSMS = (Button) v
							.findViewById(R.id.list_item_entry_btn_sms);
					holder.mBtnEmail = (Button) v
							.findViewById(R.id.list_item_entry_btn_email);
					holder.mBtnTwitter = (Button) v

					.findViewById(R.id.list_item_entry_btn_twitter);
					holder.mBtnFacebook = (Button) v
							.findViewById(R.id.list_item_entry_btn_facebook);
					holder.mBtnEvernote = (Button) v
							.findViewById(R.id.list_item_entry_btn_evernote);
					holder.mTvContent = (TextView) v
							.findViewById(R.id.list_item_entry_title);
					if (holder.mTvContent != null)
						holder.mTvContent.setText(ei.getTitle());

					// Process click events
					holder.mBtnDelete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// items.remove(position);
							// notifyDataSetChanged();
							Toast.makeText(context, "Delete",
									Toast.LENGTH_SHORT).show();
						}
					});
					holder.mBtnEvernote.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				((SwipeListView) parent).recycle(v, position);
			}
			return v;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private class ViewHolder {
		Button mBtnDelete;
		Button mBtnSMS;
		Button mBtnEmail;
		Button mBtnTwitter;
		Button mBtnFacebook;
		Button mBtnEvernote;
		TextView mTvContent;
	}

}
