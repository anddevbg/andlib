package com.anddevbg.andlib.widgets;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.anddevbg.andlib.log.LogWrapper;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class WidgetUtil {

	private WidgetUtil() {
	}

	/**
	 * 
	 * @param view
	 * @param fontPath
	 *            e.g. "fonts/niceFont.ttf"
	 */
	public static void setFontOfTextViewFromAssets(TextView view, String fontPath) {
		try {
			AssetManager assets = view.getContext().getAssets();
			view.setTypeface(Typeface.createFromAsset(assets, fontPath));
		} catch (Exception e) {
		}
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

	public static Rect getRectOfView(View v) {
		int[] position = new int[2];
		v.getLocationOnScreen(position);

		return new Rect(position[0], position[1], position[0] + v.getWidth(), position[1] + v.getHeight());
	}

	/**
	 * Convenience method to remove onGlobalLayoutListener.
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

	/**
	 * 
	 * Convenience method to enable/disable view group.
	 * 
	 * @param viewGroup
	 * @param enabled
	 */
	public static void setViewGroupEnabled(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();

		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				setViewGroupEnabled((ViewGroup) view, enabled);
			}
		}
	}

	// TODO add text view text size animation.
	/**
	 * Performs simple animation on view touch event.
	 * 
	 * @param v
	 */
	public static void makeViewClickable(View v) {
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v instanceof TextView) {
					processTextViewTouch((TextView) v, event);
				} else {
					processViewOnTouch(v, event);
				}

				return false; // false so the touch event is sent to onClickListener
			}

			private void processTextViewTouch(TextView tv, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					float textSize = tv.getTextSize();
					sTextSizeMap.put(tv.getId(), textSize);

					tv.setTextSize(textSize * 0.9f);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					tv.setTextSize(sTextSizeMap.get(tv.getId()));
				}
			}

			private void processViewOnTouch(View v, MotionEvent event) {
				PaddingCache paddingCache = sPaddingMap.get(v.getId());
				if (paddingCache == null) {
					paddingCache = new PaddingCache(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
					sPaddingMap.put(v.getId(), paddingCache);
				}

				int paddingLeft = paddingCache.paddingLeft;
				int paddingRight = paddingCache.paddingRight;
				int paddingTop = paddingCache.paddingTop;
				int paddingBottom = paddingCache.paddingBottom;

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int padding = (int) ((Math.min(v.getWidth(), v.getHeight())) * 0.1);

					v.setPadding(padding + paddingLeft, padding + paddingTop, padding + paddingRight, padding + paddingBottom);
					v.invalidate();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
					v.invalidate();
				}
			}
		});
	}

	@SuppressLint("UseSparseArrays")
	private static Map<Integer, Float> sTextSizeMap = new HashMap<Integer, Float>();

	@SuppressLint("UseSparseArrays")
	private static Map<Integer, PaddingCache> sPaddingMap = new HashMap<Integer, PaddingCache>();

	private static class PaddingCache {
		final int paddingLeft;
		final int paddingRight;
		final int paddingTop;
		final int paddingBottom;

		public PaddingCache(int left, int top, int right, int bottom) {
			paddingLeft = left;
			paddingRight = right;
			paddingTop = top;
			paddingBottom = bottom;
		}
	}
}
