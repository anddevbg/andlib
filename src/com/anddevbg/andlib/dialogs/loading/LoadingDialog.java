package com.anddevbg.andlib.dialogs.loading;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface.OnCancelListener;

import com.anddevbg.andlib.R;
import com.anddevbg.andlib.dialogs.IProgressDialog;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class LoadingDialog implements IProgressDialog {

private ProgressDialog mLoadingDialog;
	
	private boolean mIsCancelable = true;
	private int mLoadingTextResId = R.string.loading;
	
	public void show(final Activity activity) {
		if (mLoadingDialog != null && mLoadingDialog.isShowing() || activity.isFinishing()) {
			return;
		}

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLoadingDialog = ProgressDialog.show(activity, null, activity.getString(mLoadingTextResId), true, mIsCancelable);
			}
		});
	}
	
	@Override
	public void dismiss() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
		
		mLoadingDialog = null;
	}
	
	public void isCancelable(boolean isCancelable) {
		mIsCancelable = isCancelable;
	}
	
	public void resId(int resId) {
		mLoadingTextResId = resId;
	}
	
	@Override
	public void setOnCancelListener(OnCancelListener listener) {
		if (mLoadingDialog != null) {
			mLoadingDialog.setOnCancelListener(listener);
		}
	}
}
