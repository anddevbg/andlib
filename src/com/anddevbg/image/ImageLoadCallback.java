package com.anddevbg.image;

import android.graphics.Bitmap;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public interface ImageLoadCallback {
	
	public void onImageLoaded(String id, Bitmap bitmap);
	
	public void onImageLoadError();
}
