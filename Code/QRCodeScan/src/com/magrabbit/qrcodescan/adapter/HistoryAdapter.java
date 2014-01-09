package com.magrabbit.qrcodescan.adapter;

import java.util.ArrayList;

import model.HistoryItem;
import model.Item;
import model.HistorySectionItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.magrabbit.qrcodescan.R;

public class HistoryAdapter extends ArrayAdapter<Item> {

	private Context context;
	private ArrayList<Item> items;
	private LayoutInflater vi;

	public HistoryAdapter(Context context, ArrayList<Item> items) {
		super(context, 0, items);
		this.context = context;
		this.items = items;
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final Item i = items.get(position);
		if (i != null) {
			if (i.isSection()) {
				HistorySectionItem si = (HistorySectionItem) i;
				v = vi.inflate(R.layout.list_item_section, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);

				final TextView sectionView = (TextView) v
						.findViewById(R.id.list_item_section_text);
				sectionView.setText(si.getTitle());
			} else {
				HistoryItem ei = (HistoryItem) i;
				v = vi.inflate(R.layout.list_item_entry, null);
				final TextView title = (TextView) v
						.findViewById(R.id.list_item_entry_title);

				if (title != null)
					title.setText(ei.title);
			}
		}
		return v;
	}

}
