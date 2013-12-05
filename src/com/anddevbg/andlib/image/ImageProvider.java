package com.anddevbg.andlib.image;

import android.content.Context;

/**
 * 
 * @author anddevbg@gmail.com
 *
 */
public interface ImageProvider<T> {

	public void loadImages(Context ctxt, ImageLoadCallback cb, T params);

	public void cancel();

	public State getState();

	public static enum State {
		PENDING, RUNNING, FINISHED, CANCELLED;
	}
}
