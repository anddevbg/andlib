package com.anddevbg.andlib.servercommunication;

import android.os.Handler;

import com.anddevbg.andlib.log.LogWrapper;
import com.anddevbg.andlib.servercommunication.IServerRequest.State;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
class Request<T> implements Runnable {

	private IServerRequest mRequest;
	private Handler mHandler;
	private ServerRequestExecutor mExecutor;

	private ServerRequestExecutorListener<T> mCallback;

	public Request(ServerRequestExecutor executor, Handler handler, IServerRequest request, ServerRequestExecutorListener<T> callback) {
		mExecutor = executor;
		mHandler = handler;
		mRequest = request;

		mCallback = callback;
	}

	public void cancel() {
		synchronized (this) {
			mCallback = null;
		}
		
		mRequest.cancel();
	}

	@Override
	public void run() {
		if (mRequest.getState() == State.CANCELLED) {
			LogWrapper.d(ServerRequestExecutor.class, "Request is cancelled before being executed " + mRequest.getClass().getSimpleName());
			return;
		}

		final T result = mExecutor.executeServerRequestSync(mRequest);
		mExecutor.onRequestComplete(mRequest.getId());
		mRequest = null;

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				synchronized (this) {
					if (mCallback != null) {
						if (result != null) {
							mCallback.onExecute(result);
						} else {
							mCallback.onExecuteFailed();
						}
					}
				}
			}
		});
	}

}
