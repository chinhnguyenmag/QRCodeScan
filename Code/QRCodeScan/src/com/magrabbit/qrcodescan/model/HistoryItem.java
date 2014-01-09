package com.magrabbit.qrcodescan.model;


public class HistoryItem implements Item{

	public final String title;

	public HistoryItem(String title) {
		this.title = title;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
