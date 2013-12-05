package com.anddevbg.andlib.task;


public interface ITaskRunnerWorkerCallback<T> {
	
	public void onWorkerJobStart();
	
	public void onWorkerTaskComplete(TaskProgress<T> fetchProgress);
	
	public void onWorkerJobComplete(boolean isCancelled);
}
