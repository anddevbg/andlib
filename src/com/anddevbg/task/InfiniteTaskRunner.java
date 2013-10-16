package com.anddevbg.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.anddevbg.task.runner.SyncTaskRunnerWorker;

import android.os.Handler;
import android.os.Looper;

// TODO this class is not tested.
public abstract class InfiniteTaskRunner<T> extends TaskRunner<T> {

	public static final int INFINITE_TASK_ID = 666;

	private InfiniteTask mInfiniteTask;

	private Handler mHandler;

	public InfiniteTaskRunner() {
		this(null);
	}

	public InfiniteTaskRunner(ITaskRunnerCallback<T> callback) {
		super(new SyncTaskRunnerWorker<T>(), callback);

		mHandler = new Handler(Looper.getMainLooper());

		mInfiniteTask = new InfiniteTask();
		List<Task<T>> tasks = new ArrayList<Task<T>>();
		tasks.add(mInfiniteTask);
		super.setTasks(tasks);
	}

	@Override
	public void setTasks(List<Task<T>> tasks) {
	}

	public void addTask(Task<T> task) {
		mInfiniteTask.addTask(task);
	}

	public void cancelTask(Task<T> task) {
		mInfiniteTask.removeTask(task);
	}

	private class InfiniteTask extends Task<T> {

		private BlockingQueue<Task<T>> mTasks;

		public InfiniteTask() {
			super(INFINITE_TASK_ID);

			mTasks = new ArrayBlockingQueue<Task<T>>(16);
		}

		@Override
		public void execute(T updatableObject) {
			Task<T> task;

			while (true) {
				try {
					task = mTasks.take();
					task.execute(updatableObject);
				} catch (InterruptedException e) {
					e.printStackTrace();

					if (isStopped()) {
						break;
					}

					continue;
				}

				task.execute(updatableObject);

				final TaskProgress<T> fetchProgress = new TaskProgress<T>();
				fetchProgress.completedTask = task;
				fetchProgress.percentComplete = 0;

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						InfiniteTaskRunner.this.onWorkerTaskComplete(fetchProgress);
					}
				});
			}
		}

		public void addTask(Task<T> task) {
			mTasks.add(task);
		}

		public void removeTask(Task<T> task) {
			mTasks.remove(task);
		}
	}
}
