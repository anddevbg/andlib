package com.anddevbg.timer;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class SimpleTimer {

	private long mStartTime;
	private long mPassedTime;

	private boolean mIsCounting = false;

	public void startTimer() {
		mIsCounting = true;

		mStartTime = System.currentTimeMillis();
	}

	/**
	 * 
	 * @return passed time
	 */
	public void stopTimer() {
		if (mIsCounting) {
			mPassedTime = System.currentTimeMillis() - mStartTime;
		} else {
			mPassedTime = 0L;
		}

		mIsCounting = false;
	}

	/**
	 * 
	 * @return passed time from start to stop.
	 */
	public long getPassedTime() {
		return mPassedTime;
	}
}
