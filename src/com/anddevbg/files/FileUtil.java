package com.anddevbg.files;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.text.TextUtils;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class FileUtil {
	
	private FileUtil() {
		
	}
	
	/**
	 * 
	 * @return new temp file or null in case IOException is thrown during create.
	 */ 
	public static File createTempFile(Context context, String prefix) {
		if (TextUtils.isEmpty(prefix) || prefix.length() < 3) {
			prefix = "TEMP";
		}
		
		try {
			return File.createTempFile(prefix, Long.toString(System.currentTimeMillis()), context.getExternalCacheDir());
		} catch (IOException exception) {
			return null;
		}
	}
	
	public static boolean fileExist(String path) {
		return fileExist(new File(path));
	}
	
	public static boolean fileExist(File file) {
		return file.exists();
	}
	
	/**
	 * 
	 * @return directory size in bytes
	 */
	public static long getDirSize(File directory) {
		if (!directory.exists()) {
			return 0;
		}
		
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += getDirSize(file);
		}
		return length;
	}
	
	public static void deleteFileOrDir(File file) {
		if (file.isDirectory()) {
			for (File directory : file.listFiles()) {
				deleteFileOrDir(directory);
			}
		}
		file.delete();
	}
}
