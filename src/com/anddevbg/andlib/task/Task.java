package com.anddevbg.andlib.task;

public abstract class Task<T> {

	private final int mId;
	private Object mTag;
	private boolean mIsStopped;

	public Task(int id) {
		mId = id;
		
		mIsStopped = false;
	}

	/*
	 * Called from work thread
	 */
	public abstract void execute(T updatableObject);
	
	public int getId() {
		return mId;
	}
	
	public void setTag(Object tag) {
		mTag = tag;
	}
	
	public Object getTag() {
		return mTag;
	}
	
	public boolean isStopped() {
		return mIsStopped;
	}
	
	public void stop() {
		mIsStopped = true;
	}
}
