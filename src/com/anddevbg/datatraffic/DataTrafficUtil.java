package com.anddevbg.datatraffic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public class DataTrafficUtil {

	private DataTrafficUtil() {

	}

	public static boolean hasInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();

		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		
		return false;
	}
}
