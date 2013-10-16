package com.anddevbg.andlib.dialogs;

import android.app.Activity;
import android.content.DialogInterface.OnCancelListener;

public interface IProgressDialog extends IDialog {

	public void show(Activity activity, int textResId, boolean isCancelable);
	
	/**
	 * Must be called after {@link #show(Activity)}
	 * @param listener
	 */
	public void setOnCancelListener(OnCancelListener listener);
}
