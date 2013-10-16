package com.anddevbg.android;

import com.anddevbg.WidthHeight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class DisplayUtil {
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static WidthHeight getScreenSize(Context context) {
		WidthHeight result = null;
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			
			result = new WidthHeight(size.x, size.y);
		} else {
			result = new WidthHeight(display.getWidth(), display.getHeight());
		}
		
		return result;
	}
}
