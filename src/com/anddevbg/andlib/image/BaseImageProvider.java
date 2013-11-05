package com.anddevbg.andlib.image;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.anddevbg.andlib.image.Params.ImageHolder;
import com.anddevbg.andlib.log.LogWrapper;
import com.anddevbg.andlib.task.ITaskRunnerCallback;
import com.anddevbg.andlib.task.Task;
import com.anddevbg.andlib.task.TaskProgress;
import com.anddevbg.andlib.task.AbsTaskRunner;
import com.anddevbg.andlib.task.worker.SyncTaskRunnerWorker;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
abstract class BaseImageProvider<T> implements ImageProvider<T>, ITaskRunnerCallback<Params> {

	private AbsTaskRunner<Params> mTaskRunner;
	private ImageLoadCallback mCallback;
	private T mImageProviderParams;
	
	private Context mContext;

	private State mState;

	public BaseImageProvider() {
		mState = State.PENDING;
	}

	@Override
	public void loadImages(Context ctxt, ImageLoadCallback cb, T params) {
		if (mTaskRunner != null) {
			mTaskRunner.stop();
		}

		synchronized (this) {
			mState = State.RUNNING;
		}

		mContext = ctxt.getApplicationContext();
		mCallback = cb;
		mImageProviderParams = params;
		mTaskRunner = initTaskRunner();

		loadImagesAfterPrepare();
	}

	protected void loadImagesAfterPrepare() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				LogWrapper.d(BaseImageProvider.class, "Preparing...");

				prepare();

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				synchronized (this) {
					if (mState == State.CANCELLED) {
						return;
					}
				}

				LogWrapper.d(BaseImageProvider.class, "Init...");
				mTaskRunner.setTasks(getTasks());
				mTaskRunner.start();
			}
		}.execute();
	}

	public void setCallback(ImageLoadCallback callback) {
		mCallback = callback;
	}

	public void cancel() {
		synchronized (this) {
			mState = State.CANCELLED;
		}

		mCallback = null;

		if (mTaskRunner != null) {
			mTaskRunner.stop();

			mTaskRunner = null;
		}
	}

	@Override
	public State getState() {
		return mState;
	}

	protected T getParams() {
		return mImageProviderParams;
	}

	protected ImageLoadCallback getCallback() {
		return mCallback;
	}

	protected Context getContext() {
		return mContext;
	}

	/**
	 * Called before <code>getTasks()</code> method. <br>
	 * Note: It is good practice to check <code>isStopped()</code> in order to
	 * cancel prepare.
	 */
	protected abstract void prepare();

	protected abstract List<Task<Params>> getTasks();

	protected abstract void onImageLoaded(String id, Bitmap image);

	@Override
	public void onJobStart() {
	}

	@Override
	public void onProgress(TaskProgress<Params> progress) {
		ImageHolder imageHolder = progress.completedTask.getUpdatableObject().imageHolderMap.get(progress.completedTask.getId());
		if (imageHolder != null) {
			onImageLoaded(imageHolder.id, imageHolder.image);
		}
	}

	@Override
	public void onJobComplete(Params result) {
		synchronized (this) {
			mState = State.FINISHED;
		}
		
		mTaskRunner = null;
	}

	@Override
	public void onJobFailed() {
	}
	
	private AbsTaskRunner<Params> initTaskRunner() {
		return new AbsTaskRunner<Params>(new SyncTaskRunnerWorker<Params>(), this) {
	
			@Override
			protected Params createFinalResultObject() {
				return new Params();
			}
		};
	}
}
