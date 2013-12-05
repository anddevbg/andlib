package com.anddevbg.andlib.task;


/**
 * 
 * @param <T>
 *            use Object type if no result is returned.
 */
public interface ITaskRunnerCallback<T> {

	public void onJobStart();

	public void onProgress(TaskProgress<T> progress);

	public void onJobComplete(T result);

	public void onJobFailed();
}
