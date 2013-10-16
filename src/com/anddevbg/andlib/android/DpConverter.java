package com.anddevbg.andlib.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class DpConverter {

	public static float convertDpToPixel(Context context, float dp) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}
	
	public static float convertPixelsToDp(Context context, float px) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}
}
