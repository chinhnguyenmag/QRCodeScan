package model;


public class HistoryItem implements Item{

	public final String title;
	public final String subtitle;

	public HistoryItem(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
