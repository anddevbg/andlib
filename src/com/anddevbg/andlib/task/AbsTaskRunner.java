package com.anddevbg.andlib.task;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;

import com.anddevbg.andlib.log.LogWrapper;
import com.anddevbg.andlib.task.worker.SyncTaskRunnerWorker;

/**
 * 
 * Convenience class to execute sequence of tasks for a result.
 * 
 * @author anddevbg@gmail.com
 * 
 */
public abstract class AbsTaskRunner<T> implements ITaskRunnerWorkerCallback<T> {

	private List<Task<T>> mTasks;
	private ITaskRunnerWorker<T> mWorker;
	private ITaskRunnerCallback<T> mCallback;

	public AbsTaskRunner() {
		this(new SyncTaskRunnerWorker<T>(), null);
	}

	public AbsTaskRunner(ITaskRunnerWorker<T> worker, ITaskRunnerCallback<T> callback) {
		synchronized (this) {
			mCallback = callback;
		}

		mWorker = worker;
		mWorker.setCallback(this);
	}

	public void setTasks(List<Task<T>> tasks) {
		if (mWorker.getState() != State.PENDING) {
			throw new RuntimeException("Tasks already has been executed or are in process of executing.");
		}

		mTasks = new ArrayList<Task<T>>(tasks);
	}

	@SuppressLint("NewApi")
	public synchronized void start() {
		LogWrapper.d(this, "---- Start ----");

		if (mTasks == null || mTasks.isEmpty()) {
			throw new RuntimeException("Task list is empty.");
		}

		mWorker.start(mTasks);
	}

	public synchronized void stop() {
		LogWrapper.d(this, "---- Stop ----");

		if (mWorker.getState() == State.RUNNING) {
			mCallback = null;
			mWorker.stop();
		}
	}

	public void setCallback(ITaskRunnerCallback<T> callback) {
		synchronized (this) {
			mCallback = callback;
		}
	}

	@Override
	public void onWorkerJobStart() {
		synchronized (this) {
			if (mCallback != null) {
				mCallback.onJobStart();
			}
		}
	}

	@Override
	public void onWorkerTaskComplete(TaskProgress<T> fetchProgress) {
		synchronized (this) {
			if (mCallback != null) {
				mCallback.onProgress(fetchProgress);
			}
		}
	}

	@Override
	public void onWorkerJobComplete(boolean isCancelled) {
		synchronized (this) {
			if (mCallback != null) {
				T resultObject = createFinalResultObject();
				if (resultObject != null) {
					mCallback.onJobComplete(resultObject);
				} else {
					mCallback.onJobFailed();
				}
			}
		}
	}
	
	protected abstract T createFinalResultObject();

	protected ITaskRunnerWorker<T> getWorker() {
		return mWorker;
	}
}
