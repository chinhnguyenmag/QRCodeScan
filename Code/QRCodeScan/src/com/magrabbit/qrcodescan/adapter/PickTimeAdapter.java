package com.magrabbit.qrcodescan.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.magrabbit.qrcodescan.R;

import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * @author Hung Hoang
 * 
 */
public class PickTimeAdapter extends NumericWheelAdapter {
	// Items step value
	private int mStep;
	private String mValue;

	/**
	 * Constructor
	 */
	public PickTimeAdapter(Context context, int maxValue, int step) {
		super(context, 0, maxValue / step);
		this.mStep = step;
		setItemResource(R.layout.wheel_text_item);
		setItemTextResource(R.id.text);
	}

	@Override
	public CharSequence getItemText(int index) {
		if (index >= 0 && index < getItemsCount()) {
			int value = index * mStep;
			if (value == 95) {
				return "Nerver";
			}
			return Integer.toString(value);
		}
		return null;
	}
	
	public CharSequence getValueItem(int index){
		return getItemText(index);
	}
}
