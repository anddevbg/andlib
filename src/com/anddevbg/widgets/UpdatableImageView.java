package com.anddevbg.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.anddevbg.image.ImageLoadCallback;
import com.anddevbg.image.ImageProvider;
import com.anddevbg.log.LogWrapper;

// TODO add default image
/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class UpdatableImageView extends ImageView implements ImageLoadCallback {
	
	private final Object mCallbackLock = new Object();
	
	private ImageProvider<?> mImageProvider;

	private boolean mIsLoading; // Guarded by this
	private boolean mFailedToUpdate; // Guarded by this
	
	private UpdatableImageCallback mCallback;
	
	private boolean mInvalidateOnLoad = true;
	
	public UpdatableImageView(Context context) {
		super(context);

	}

	public UpdatableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public void setCallback(UpdatableImageCallback callback) {
		synchronized (mCallbackLock) {
			mCallback = callback;
		}
	}

	public void invalidateOnLoad(boolean value) {
		mInvalidateOnLoad = value;
	}

	@Override
	public void onImageLoaded(String id, final Bitmap bitmap) {
		LogWrapper.d(this, "OnImageLoaded: " + id + " size:  " + (bitmap != null ? bitmap.getRowBytes() * bitmap.getHeight() : 0));
		
		synchronized (this) {
			mIsLoading = false;
			mFailedToUpdate = false;
		}
		
		post(new Runnable() {

			@Override
			public void run() {
				if (bitmap != null) {
					setImageBitmap(bitmap);
				}
				
				if (mInvalidateOnLoad) {
					invalidate();
				}

				synchronized (mCallbackLock) {
					if (mCallback != null) {
						mCallback.onUpdate();
					}
				}
			}
		});
	}

	@Override
	public void onImageLoadError() {
		LogWrapper.d(this, "On image load error.");
		synchronized (this) {
			mIsLoading = false;
		}
	}
	
	public void cancelUpdateIfRunning() {
		if (mImageProvider != null) {
			mImageProvider.cancel();
		}
	}
	
	/**
	 * Automatically loads images.
	 *  
	 * @param provider
	 * @param params
	 */
	public <T> void setImageLoader(ImageProvider<T> provider, T params) {
		synchronized (this) {
			if (mIsLoading) {
				LogWrapper.d(this, "Image is still loading...");
				return;
			}
		}

		if (mImageProvider != null) {
			if (mFailedToUpdate) {
				LogWrapper.d(this, "Already failed to update.Cancelling loading.");
				/*
				 * Default images should be loaded
				 */
				return;
			}
			
			if (mImageProvider != provider) {
				mImageProvider.cancel();
			}
		}
		synchronized (this) {
			mIsLoading = true;
			mFailedToUpdate = false;
		}
		
		mImageProvider = provider;
		LogWrapper.d(this, "Start loading images.");
		provider.loadImages(getContext(), this, params);
	}

	@Override
	protected void onDetachedFromWindow() {
		clear();
		setCallback(null);

		super.onDetachedFromWindow();
	}
	
	@Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        
        invalidate();
    }
	
	private void clear() {
		synchronized (this) {
			mIsLoading = false;
			mFailedToUpdate = false;
		}
		
		if (mImageProvider != null) {
			setImageBitmap(null);
			mImageProvider.cancel();

			mImageProvider = null;
		}
	}

	public static interface UpdatableImageCallback {

		public void onUpdate();
	}
}
