package com.anddevbg.andlib.task;

import java.util.List;

public interface ITaskRunnerWorker<T> {
	
	public void setCallback(ITaskRunnerWorkerCallback<T> callback);
	
	public void start(List<Task<T>> tasks);
	
	public void stop();
	
	public State getState();
}
