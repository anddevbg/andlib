package com.anddevbg.andlib.strings;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Patterns;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class StringUtil {

	public static List<String> retreiveLinksFromText(String text) {
		List<String> result = new ArrayList<String>();

		Matcher m = Patterns.WEB_URL.matcher(text);
		try {
			while (m.find()) {
				result.add(m.group());
			}
		} catch (Exception e) {
		}

		return result;
	}

	@SuppressLint("DefaultLocale")
	public static String capitalizeLetters(int start, int end, String text) {
		String result = text;

		if (!TextUtils.isEmpty(result)) {
			try {
				result = result.substring(start, end).toUpperCase() + result.substring(end);
			} catch (IndexOutOfBoundsException e) {
				return text;
			}
		}

		return result;
	}
	
	/**
	 * 
	 * @return SHA-1 hex string or null if exception is thrown.
	 */
	public static String toSHA1(String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			byte[] sha1hash = md.digest();

			return convertToHex(sha1hash);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static String toSHA1(String text, String salt) {
		return toSHA1(text + salt);
	}

	private static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (byte b : data) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}

