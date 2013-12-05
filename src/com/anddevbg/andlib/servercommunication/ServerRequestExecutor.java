package com.anddevbg.andlib.servercommunication;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class ServerRequestExecutor {

	private static final int EXECUTOR_THREAD_POOL_SIZE = 2;

	private static ServerRequestExecutor sInstance = new ServerRequestExecutor();

	public static ServerRequestExecutor getInstance() {
		return sInstance;
	}

	private Map<String, Request<?>> mRequests = new Hashtable<String, Request<?>>();

	private ExecutorService mExecutor;
	private Handler mHandler;

	private ServerRequestExecutor() {
		mExecutor = Executors.newFixedThreadPool(EXECUTOR_THREAD_POOL_SIZE);
		mHandler = new Handler(Looper.getMainLooper());
	}

	/**
	 * Return null if request fails or response object.
	 * 
	 * @param request
	 * @return
	 */
	public <T> T executeServerRequestSync(IServerRequest request) {
		try {
			return request.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Result will be delivered on UI thread.
	 */
	public synchronized <T> void executeServerRequest(IServerRequest request, ServerRequestExecutorListener<T> callback) {
		executeServerRequest(mHandler, request, callback);
	}

	public synchronized <T> void executeServerRequest(Handler handler, IServerRequest request, ServerRequestExecutorListener<T> callback) {
		if (callback == null) {
			throw new RuntimeException("Callback is null");
		}

		Request<T> Request = new Request<T>(sInstance, handler, request, callback);

		Request<?> previous = mRequests.put(request.getId(), Request);
		if (previous != null) {
			cancelRequest(previous);
		}

		mExecutor.execute(Request);
	}

	public synchronized void cancelRequest(IServerRequest request) {
		if (mRequests.containsValue(request.getId())) {
			cancelAsyncRequest(request.getId());
		} else {
			cancelSyncRequest(request);
		}
	}

	/**
	 * Cancels request started with
	 * <code>executeServerRequestSync(request)</code>
	 */
	private void cancelSyncRequest(final IServerRequest request) {
		mExecutor.execute(new Runnable() {

			@Override
			public void run() {
				request.cancel();
			}
		});
	}

	/**
	 * Cancels previously added request with
	 * <code>executeServerRequest(request, callback)</code>
	 */
	private void cancelAsyncRequest(String requestId) {
		if (mRequests.containsKey(requestId)) {
			cancelRequest(mRequests.get(requestId));
			mRequests.remove(requestId);
		}
	}

	protected void onRequestComplete(String requestId) {
		mRequests.remove(requestId);
	}

	private void cancelRequest(final Request<?> request) {
		if (request != null) {
			mExecutor.execute(new Runnable() {

				@Override
				public void run() {
					request.cancel();
				}
			});
		}
	}

}
