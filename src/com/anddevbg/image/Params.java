package com.anddevbg.image;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class Params {

	public Map<Integer, ImageHolder> imageHolderMap = new HashMap<Integer, Params.ImageHolder>();

	public static class ImageHolder {
		public String id;
		public Bitmap image;
	}
}
