package com.anddevbg.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Surface;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class ActivityUtil {

	private ActivityUtil() {

	}

	@SuppressLint("NewApi")
	public static void lockScreenOrientation(Activity activity) {
		switch (activity.getResources().getConfiguration().orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else {
					int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
					if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_180) {
						activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
					} else {
						activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
				}
				break;

			case Configuration.ORIENTATION_LANDSCAPE:
				if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {
					int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
					if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
						activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					} else {
						activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					}
				}
				break;
		}
	}
	
	public static void unlockScreenOrientation(Activity activity) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
}
