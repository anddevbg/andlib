package com.anddevbg.task;

import java.util.List;

import android.os.AsyncTask.Status;

public interface ITaskRunnerWorker<T> {
	
	public void setCallback(ITaskRunnerWorkerCallback<T> callback);
	
	public void start(T resultObject, List<Task<T>> tasks);
	
	public void stop();
	
	public Status getStatus();
}
