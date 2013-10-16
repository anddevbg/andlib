package com.anddevbg.andlib.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.anddevbg.andlib.log.LogWrapper;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class WidgetUtil {

	private WidgetUtil() {
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void setBackgroundDrawable(View view, Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);
		}
	}

	public static Drawable getBackgroundDrawable(Context context, int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

		if (bitmap == null) {
			return context.getResources().getDrawable(resId);
		}

		byte[] chunk = bitmap.getNinePatchChunk();
		if (NinePatch.isNinePatchChunk(chunk)) {
			return new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
		} else {
			return new BitmapDrawable(context.getResources(), bitmap);
		}
	}

	public static int goneOrVisible(boolean isVisible) {
		return isVisible ? View.VISIBLE : View.GONE;
	}

	public static int invisibleOrVisible(boolean isVisible) {
		return isVisible ? View.VISIBLE : View.INVISIBLE;
	}

	public static boolean isVisible(View view) {
		return view.getVisibility() == View.VISIBLE;
	}

	public static boolean isInvisible(View view) {
		return view.getVisibility() == View.INVISIBLE;
	}

	public static boolean isGone(View view) {
		return view.getVisibility() == View.GONE;
	}

	/**
	 * 
	 * @return null if squareSide > 0, OnGlobalLayoutListener otherwise. <br>
	 * 
	 * @see #removeOnGlobalLayoutListener(View, OnGlobalLayoutListener)
	 * 
	 */
	public static final OnGlobalLayoutListener squareView(final View view, int squareSide) {
		if (squareSide > 0) {
			android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			layoutParams.width = squareSide;
			layoutParams.height = squareSide;
			view.setLayoutParams(layoutParams);
		} else {
			final OnGlobalLayoutListener listener = new OnGlobalLayoutListener() {

				@Override
				public void onGlobalLayout() {
					LogWrapper.d(WidgetUtil.class, "onGlobalLayout()");
					int squareSide = view.getMeasuredWidth();

					if (squareSide == 0) {
						return;
					}
					
					WidgetUtil.removeOnGlobalLayoutListener(view, this);
					
					LayoutParams layoutParams = view.getLayoutParams();
					layoutParams.width = squareSide;
					layoutParams.height = squareSide;
					view.setLayoutParams(layoutParams);
				}
			};
			view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
			
			return listener;
		}
		
		return null;
	}
	
	/**
	 * Convenient method to remove onGlobalLayoutListener
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void removeOnGlobalLayoutListener(View view, OnGlobalLayoutListener listener) {
		LogWrapper.d(WidgetUtil.class, "removeOnGlobalLayoutListener()");
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		} else {
			view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
		}
	}
}
