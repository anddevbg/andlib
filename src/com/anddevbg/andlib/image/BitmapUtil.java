package com.anddevbg.andlib.image;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.anddevbg.andlib.WidthHeight;
import com.anddevbg.andlib.log.LogWrapper;
import com.anddevbg.andlib.sdcard.SdCardUtil;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class BitmapUtil {

	/**
	 * Same as <code>loadBitmapFromURL(url, imageFilePath, -1, -1);</code> 
	 */
	public static Bitmap loadBitmapFromURL(String url, String imageFilePath) {
		return loadBitmapFromURL(url, imageFilePath, -1, -1);
	}
	
	/**
	 * 
	 * @param reqWidth pass -1 to decode with original size
	 * @param reqHeight pass -1 to decode with original size
	 * @return Bitmap with requested size.
	 */
	public static Bitmap loadBitmapFromURL(String url, String imageFilePath, int reqWidth, int reqHeight) {
		LogWrapper.d(BitmapUtil.class, "Loading bitmap for: " + url);
		
		if (!isSDCardAcailable()) {
			return null;
		}
		
		boolean downloadedSuccessfully = true;
		InputStream in = null;
		BufferedOutputStream out = null;

		
		final int bufferSize = 4 * 1024;
		try {
			in = new BufferedInputStream(new URL(url).openStream(), bufferSize);
			final FileOutputStream dataStream = new FileOutputStream(imageFilePath);
			out = new BufferedOutputStream(dataStream, bufferSize);
			IOUtils.copy(in, out);
			out.flush();
		} catch (IOException e) {
			downloadedSuccessfully = false;
			
			e.printStackTrace();
			LogWrapper.e(BitmapUtil.class, "Could not load Bitmap from: " + url);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		
		return downloadedSuccessfully ? loadBitmapFromFile(imageFilePath, reqWidth, reqHeight) : null;
	}
	
	private static boolean isSDCardAcailable() {
		boolean sdCardAvailableForWrite = SdCardUtil.isAvailableForWrite();
		if (!sdCardAvailableForWrite) {
			LogWrapper.d(BitmapUtil.class, "Sd card is not available");
			return false;
		}
		return true;
	}
	
	/**
	 * Same as <code>loadBitmapFromRes(context, resId, -1, -1);</code> 
	 */
	public static Bitmap loadBitmapFromRes(Context context, int resId) {
		return loadBitmapFromRes(context, resId, -1, -1);
	}
	
	/**
	 * 
	 * @param reqWidth pass -1 to decode with original size
	 * @param reqHeight pass -1 to decode with original size
	 * @return Bitmap with requested size.
	 */
	public static Bitmap loadBitmapFromRes(Context context, int resId, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = null;
		if (reqWidth != -1 && reqHeight != -1) {
			options = new BitmapFactory.Options();
			
			WidthHeight originalSize = getBitmapSize(context, resId);
			options.inSampleSize = getBitmapInSampleSize(originalSize.width, originalSize.height, reqWidth, reqHeight);
		}
		
		return BitmapFactory.decodeResource(context.getResources(), resId, options);
	}
	
	/**
	 * Same as <code>loadBitmapFromFile(path, -1, -1);</code>
	 */
	public static Bitmap loadBitmapFromFile(String path) {
		return loadBitmapFromFile(path, -1, -1);
	}
	
	/**
	 * 
	 * @param path to bitmap
	 * @param reqWidth pass -1 to decode with original size
	 * @param reqHeight pass -1 to decode with original size
	 * @return Bitmap with requested size.
	 */
	public static Bitmap loadBitmapFromFile(String path, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = null;
		if (reqWidth != -1 && reqHeight != -1) {
			options = new BitmapFactory.Options();
			
			WidthHeight originalSize = getBitmapSize(path);
			options.inSampleSize = getBitmapInSampleSize(originalSize.width, originalSize.height, reqWidth, reqHeight);
		}
		
		Bitmap result = BitmapFactory.decodeFile(path, options);
		
		LogWrapper.d(BitmapUtil.class, "loading bitmap from file: " + path);
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			
			Bitmap rotatedBitmap = rotateBitmap(result, orientation);
			result = rotatedBitmap == null ? result : rotatedBitmap;
		} catch (IOException e) {
		}		
		
		//if no EXIF return the picture normally
		return result;
	}
	
	private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
		if (bitmap == null) {
			return null;
		}
		
		Matrix matrix = new Matrix();
		if (orientation ==ExifInterface.ORIENTATION_NORMAL) {
			return bitmap;
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
			matrix.postRotate(90);
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
		    matrix.postRotate(180);
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
		    matrix.postRotate(270);
		}
		
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
	}
	
	public static boolean isPortrait(Bitmap bitmap) {
		return bitmap.getHeight() > bitmap.getWidth();
	}
	
	public static WidthHeight getBitmapSize(Context context, int imageId) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), imageId, options);
		
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		
		return new WidthHeight(imageWidth, imageHeight);
	}
	
	public static WidthHeight getBitmapSize(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		
		int imageWidth = options.outWidth;
		int imageHeight = options.outHeight;
		
		return new WidthHeight(imageWidth, imageHeight);
	}
	
	public static int getBitmapInSampleSize(int bitmapWidth, int bitmapHeight, int reqWidth, int reqHeight) {
		int inSampleSize = 1;

	    if (bitmapHeight > reqHeight || bitmapWidth > reqWidth) {

	        final int heightRatio = Math.round((float) bitmapHeight / (float) reqHeight);
	        final int widthRatio = Math.round((float) bitmapWidth / (float) reqWidth);

	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	
	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			bitmap.recycle();
		}
	}
	
}
