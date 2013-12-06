package com.anddevbg.andlib.common.tasks;

import android.app.Activity;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.anddevbg.andlib.dialogs.IProgressDialog;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public abstract class DialogTask<T> extends AsyncTask<Void, Void, T> implements OnCancelListener {

	private IProgressDialog mDialog;
	private Activity mActivity;

	public DialogTask(Activity activity, IProgressDialog dialog) {
		mActivity = activity;
		mDialog = dialog;
	}
	
	protected Activity getActivity() {
		return mActivity;
	}
	
	protected IProgressDialog getDialog() {
		return mDialog;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mDialog.show(mActivity);
		mDialog.setOnCancelListener(this);
	}

	@Override
	protected void onPostExecute(T result) {
		super.onPostExecute(result);

		mDialog.dismiss();
	}
	
	public void stop() {
		mDialog.dismiss();
	}
}
