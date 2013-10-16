package com.anddevbg.sdcard;

import android.os.Environment;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class SdCardUtil {
	
	private SdCardUtil() {
		
	}

	public static boolean isAvailableForRead() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isAvailableForWrite() {
		return (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()));
	}
}
