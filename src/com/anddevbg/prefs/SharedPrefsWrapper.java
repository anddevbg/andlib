package com.anddevbg.prefs;

import com.anddevbg.log.LogWrapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
@SuppressLint("CommitPrefEdits")
public class SharedPrefsWrapper {

	private String mPrefsName;
	private Context mContext;

	private SharedPreferences.Editor mEditor;

	public SharedPrefsWrapper(Context context) {
		this(context, null);
	}

	public SharedPrefsWrapper(Context context, String name) {
		mPrefsName = name;

		mContext = context.getApplicationContext();
	}

	public boolean getBooleanPref(String key, boolean defaultValue) {
		SharedPreferences prefs = getPrefs();

		return prefs.getBoolean(key, defaultValue);
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 *            null to delete existing value.
	 */
	public void setBooleanPref(String key, Boolean value) {
		SharedPreferences.Editor editor = getEditor();
		if (value != null) {
			editor.putBoolean(key, value);
		} else {
			editor.remove(key);
		}
	}

	public int getIntegerPref(String key, int defaultValue) {
		SharedPreferences prefs = getPrefs();

		return prefs.getInt(key, defaultValue);
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 *            null to delete existing value.
	 */
	public void setIntegerPref(String key, Integer value) {
		SharedPreferences.Editor editor = getEditor();
		if (value != null) {
			editor.putInt(key, value);
		} else {
			editor.remove(key);
		}
	}

	public long getLongPref(String key, long defaultValue) {
		SharedPreferences prefs = getPrefs();

		return prefs.getLong(key, defaultValue);
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 *            null to delete existing value.
	 */
	public void setLongPref(String key, Long value) {
		SharedPreferences.Editor editor = getEditor();
		if (value != null) {
			editor.putLong(key, value);
		} else {
			editor.remove(key);
		}
	}

	public String getStringPref(String key, String defaultValue) {
		SharedPreferences prefs = getPrefs();

		return prefs.getString(key, defaultValue);
	}

	/**
	 * 
	 * @param context
	 * @param key
	 * @param value
	 *            null to delete existing value.
	 */
	public void setStringPref(String key, String value) {
		SharedPreferences.Editor editor = getEditor();
		if (value != null) {
			editor.putString(key, value);
		} else {
			editor.remove(key);
		}
	}

	public boolean contains(String key) {
		return getPrefs().contains(key);
	}

	public void ereaseAllPreviousData() {
		getEditor().clear();
	}

	public void commit() {
		if (mEditor != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				LogWrapper.d(this, "Applying changes");

				mEditor.apply();
			} else {
				LogWrapper.d(this, "Commiting changes");

				mEditor.commit();
			}
		}

		mEditor = null;
	}

	private SharedPreferences getPrefs() {
		if (!TextUtils.isEmpty(mPrefsName)) {
			return mContext.getSharedPreferences(mPrefsName, Context.MODE_PRIVATE);
		} else {
			return PreferenceManager.getDefaultSharedPreferences(mContext);
		}
	}

	private SharedPreferences.Editor getEditor() {
		if (mEditor != null) {
			return mEditor;
		} else {
			mEditor = getPrefs().edit();
		}

		return mEditor;
	}
}
