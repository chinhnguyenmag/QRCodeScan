package com.magrabbit.qrcodescan.social.twitter;

public interface TwitterDialogListener {
	public void onComplete(String value);

	public void onError(String value);
}
