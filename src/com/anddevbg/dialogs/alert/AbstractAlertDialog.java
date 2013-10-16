package com.anddevbg.dialogs.alert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import com.anddevbg.dialogs.IDialog;

/**
 * Simple alert dialog.
 * 
 * @author anddevbg@gmail.com
 * 
 */
abstract class AbstractAlertDialog implements IDialog {

	private AlertDialog mDialog;

	protected abstract void build(Builder builder);

	@Override
	public void show(Activity activity) {
		if (mDialog != null && mDialog.isShowing()) {
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		build(builder);
		mDialog = builder.create();
		mDialog.show();
	}

	@Override
	public void dismiss() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}

		mDialog = null;
	}
}
