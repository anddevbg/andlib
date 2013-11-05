package com.anddevbg.andlib.task;

public abstract class Task<T> {

	private final int mId;
	
	private final T mUpdatableObject;
	
	private boolean mIsStopped;

	public Task(int id, T updatableObject) {
		mId = id;
		mUpdatableObject = updatableObject;
		
		mIsStopped = false;
	}

	/**
	 * Must be called on work thread.
	 */
	public abstract void execute();
	
	public int getId() {
		return mId;
	}
	
	public synchronized boolean isStopped() {
		return mIsStopped;
	}
	
	public synchronized void stop() {
		mIsStopped = true;
	}
	
	public T getUpdatableObject() {
		return mUpdatableObject;
	}
}
