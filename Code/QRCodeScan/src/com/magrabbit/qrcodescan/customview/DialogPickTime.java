package com.magrabbit.qrcodescan.customview;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.magrabbit.qrcodescan.R;
import com.magrabbit.qrcodescan.adapter.PickTimeAdapter;
import com.magrabbit.qrcodescan.utils.Utils;

/**
 * 
 * @author Hung Hoang
 * 
 */
public class DialogPickTime extends BaseDialog implements OnClickListener {
	private Button mBtOk;
	private Button mBtCancel;
	private WheelView mWhellviewPickTime;
	private PickTimeAdapter mPickTimeAdapter;
	private ProcessDialogPickTime mProcess;
	private int mValue = -1;

	/**
	 * 
	 * 
	 * @param context
	 * @param arrMenu
	 */
	public DialogPickTime(Context context, ProcessDialogPickTime process,
			int currentValue) {
		super(context);
		this.mProcess = process;
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.dialog_select_time);
		mBtOk = (Button) findViewById(R.id.dialog_select_time_bt_Ok);
		mBtCancel = (Button) findViewById(R.id.dialog_select_time_bt_Cancel);
		mBtOk.setOnClickListener(this);
		mBtCancel.setOnClickListener(this);

		mWhellviewPickTime = (WheelView) findViewById(R.id.dialog_select_time_wv_time);
		mPickTimeAdapter = new PickTimeAdapter(context, 95, 5);
		mWhellviewPickTime.setViewAdapter(mPickTimeAdapter);

		if (currentValue == -1) {
			mWhellviewPickTime.setCurrentItem(19);
		} else {
			mWhellviewPickTime
					.setCurrentItem(Utils.parseIndex(currentValue, 5));
		}

		mWhellviewPickTime.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!mPickTimeAdapter.getItemText(wheel.getCurrentItem())
						.equals("Nerver")) {
					mValue = Integer.parseInt(mPickTimeAdapter.getItemText(
							wheel.getCurrentItem()).toString());
				} else {
					mValue = -1;
				}
			}
		});

	}

	public static abstract class ProcessDialogPickTime {
		public abstract void click_Ok(int value);

		public abstract void click_Cancel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_select_time_bt_Ok:
			mProcess.click_Ok(mValue);
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
