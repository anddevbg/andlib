package com.anddevbg.andlib.task.runner;

import java.util.List;

import android.os.AsyncTask;
import android.os.Build;

import com.anddevbg.andlib.task.ITaskRunnerWorker;
import com.anddevbg.andlib.task.ITaskRunnerWorkerCallback;
import com.anddevbg.andlib.task.Task;
import com.anddevbg.andlib.task.TaskProgress;

public class SyncTaskRunnerWorker<T> extends AsyncTask<Void, TaskProgress<T>, Void> implements ITaskRunnerWorker<T> {

	private Task<T> mCurrentTask;
	private ITaskRunnerWorkerCallback<T> mCallback;
	private List<Task<T>> mTasks;
	private T mResultObject;

	@Override
	public void start(T resultObject, List<Task<T>> tasks) {
		if (getStatus() == Status.PENDING) {
			mResultObject = resultObject;
			mTasks = tasks;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				execute();
			}
		}
	}

	public void stop() {
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
			mCurrentTask.execute(mResultObject);

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

		if (mCallback != null) {
			mCallback.onWorkerJobComplete(isCancelled());
		}
	}
}
