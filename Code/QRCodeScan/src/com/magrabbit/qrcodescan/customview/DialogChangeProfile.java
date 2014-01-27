package com.magrabbit.qrcodescan.customview;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.magrabbit.qrcodescan.R;

/**
 * 
 * @author Hung Hoang
 * 
 */
public class DialogChangeProfile extends BaseDialog implements OnClickListener {
	private Button mBtOk;
	private Button mBtCancel;
	private EditText mEtUrlProfile;
	private ProcessDialogProfile mProcess;
	private String mUrl = "";

	/**
	 * 
	 * 
	 * @param context
	 * @param arrMenu
	 */
	public DialogChangeProfile(Context context, ProcessDialogProfile process,
			int currentValue) {
		super(context);
		this.mProcess = process;
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_change_profile);
		mBtOk = (Button) findViewById(R.id.dialog_change_profile_bt_Ok);
		mBtCancel = (Button) findViewById(R.id.dialog_change_profile_bt_Cancel);
		mEtUrlProfile = (EditText) findViewById(R.id.dialog_change_profile_et_url);
		mBtOk.setOnClickListener(this);
		mBtCancel.setOnClickListener(this);

	}

	public static abstract class ProcessDialogProfile {
		public abstract void click_Ok(String url);

		public abstract void click_Cancel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_select_time_bt_Ok:
			mProcess.click_Ok(mUrl);
			dismiss();
			break;
		case R.id.dialog_select_time_bt_Cancel:
			mProcess.click_Cancel();
			dismiss();
			break;
		default:
			break;
		}
	}
}
