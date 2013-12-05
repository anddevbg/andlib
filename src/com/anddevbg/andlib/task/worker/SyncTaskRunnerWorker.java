package com.anddevbg.andlib.task.worker;

import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.anddevbg.andlib.task.ITaskRunnerWorker;
import com.anddevbg.andlib.task.ITaskRunnerWorkerCallback;
import com.anddevbg.andlib.task.State;
import com.anddevbg.andlib.task.Task;
import com.anddevbg.andlib.task.TaskProgress;

public class SyncTaskRunnerWorker<T> extends AsyncTask<Void, TaskProgress<T>, Void> implements ITaskRunnerWorker<T> {

	private Task<T> mCurrentTask;
	private ITaskRunnerWorkerCallback<T> mCallback;
	private List<Task<T>> mTasks;

	private State mState;

	public SyncTaskRunnerWorker() {
		mState = State.PENDING;
	}

	@Override
	public void start(List<Task<T>> tasks) {
		synchronized (this) {
			if (mState == State.PENDING) {
				mState = State.RUNNING;

				mTasks = tasks;

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					execute();
				}
			}
		}
	}

	public void stop() {
		synchronized (this) {
			mState = State.CANCELLED;
		}

		if (mCurrentTask != null) {
			mCurrentTask.stop();
		}

		cancel(true);
	}

	@Override
	public void setCallback(ITaskRunnerWorkerCallback<T> callback) {
		mCallback = callback;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (mCallback != null) {
			mCallback.onWorkerJobStart();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(Void... params) {
		int progress = 100 / mTasks.size();

		for (int i = 0; i < mTasks.size(); i++) {
			if (isCancelled()) {
				break;
			}

			mCurrentTask = mTasks.get(i);
			mCurrentTask.execute();

			if (isCancelled()) {
				break;
			}

			TaskProgress<T> fetchProgress = new TaskProgress<T>();
			fetchProgress.completedTask = mCurrentTask;
			fetchProgress.percentComplete = progress * (i + 1);

			publishProgress(fetchProgress);
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(TaskProgress<T>... values) {
		super.onProgressUpdate(values);
		TaskProgress<T> progress = values[0];

		if (progress != null && mCallback != null) {
			mCallback.onWorkerTaskComplete(progress);
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		synchronized (this) {
			mState = State.FINISHED;
		}

		if (mCallback != null) {
			mCallback.onWorkerJobComplete(isCancelled());
		}
	}

	@Override
	public synchronized State getState() {
		return mState;
	}
}
