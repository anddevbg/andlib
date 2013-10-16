package com.anddevbg.andlib.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class LogWrapper {

	public static boolean SHOW_LOGS = true;
	
	private LogWrapper() {

	}
	
	public static void disableLogs() {
		SHOW_LOGS = false;
	}
	
	public static void enableLogs() {
		SHOW_LOGS = true;
	}
	
	public static void d(Object tag, String message) {
		d(tag.getClass(), message);
	}

	public static void i(Object tag, String message) {
		i(tag.getClass(), message);
	}

	public static void w(Object tag, String message) {
		w(tag.getClass(), message);
	}

	public static void e(Object tag, String message) {
		e(tag.getClass(), message);
	}

	public static void v(Object tag, String message) {
		v(tag.getClass(), message);
	}

	public static void wtf(Object tag, String message) {
		wtf(tag.getClass(), message);
	}

	public static void d(Class<?> tag, String message) {
		d(tag.getSimpleName(), message);
	}

	public static void i(Class<?> tag, String message) {
		i(tag.getSimpleName(), message);
	}

	public static void w(Class<?> tag, String message) {
		w(tag.getSimpleName(), message);
	}

	public static void e(Class<?> tag, String message) {
		e(tag.getSimpleName(), message);
	}

	public static void v(Class<?> tag, String message) {
		v(tag.getSimpleName(), message);
	}

	public static void wtf(Class<?> tag, String message) {
		wtf(tag.getSimpleName(), message);
	}

	public static void d(String tag, String message) {
		if (SHOW_LOGS) {
			Log.d(tag, ensureMessageIsNotEmpty(message));
		}
	}

	public static void i(String tag, String message) {
		if (SHOW_LOGS) {
			Log.i(tag, ensureMessageIsNotEmpty(message));
		}
	}

	public static void w(String tag, String message) {
		if (SHOW_LOGS) {
			Log.w(tag, ensureMessageIsNotEmpty(message));
		}
	}

	public static void e(String tag, String message) {
		if (SHOW_LOGS) {
			Log.e(tag, ensureMessageIsNotEmpty(message));
		}
	}

	public static void v(String tag, String message) {
		if (SHOW_LOGS) {
			LogWrapper.v(tag, ensureMessageIsNotEmpty(message));
		}
	}

	public static void wtf(String tag, String message) {
		if (SHOW_LOGS) {
			LogWrapper.wtf(tag, ensureMessageIsNotEmpty(message));
		}
	}

	private static String ensureMessageIsNotEmpty(String message) {
		if (TextUtils.isEmpty(message)) {
			return "Error: message is empty.";
		} else {
			return message;
		}
	}
}
