package com.anddevbg.andlib.servercommunication;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.anddevbg.andlib.log.LogWrapper;
import com.anddevbg.andlib.servercommunication.IServerRequest;
import com.anddevbg.andlib.timer.SimpleTimer;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class ServerRequester<T> implements IServerRequest {

	private static final int CONNECTION_TIMEOUT = 15000; // 15 sec
	private static final int SO_TIMEOUT = 15000; // 15 sec

	private final Object mLock = new Object();

	private HttpUriRequest mRequest;

	private String mId;
	private String mUrl;
	private String mPostBody;

	private ResponseParser<T> mResponseParser;

	private State mState;

	public ServerRequester(String id, String url, ResponseParser<T> parser) {
		this(id, url, null, parser);
	}

	public ServerRequester(String id, String url, String body, ResponseParser<T> parser) {
		mState = State.PENDING;

		mId = id;
		mUrl = url;
		mPostBody = body;

		mResponseParser = parser;
	}

	@Override
	public String getId() {
		return mId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T execute() throws ClientProtocolException, IOException {
		synchronized (mLock) {
			mState = State.RUNNING;
		}
		
		SimpleTimer timer = new SimpleTimer();
		timer.startTimer();
		
		T result = null;
		if (mPostBody == null) {
			result = doGet();
		} else {
			result = doPost();
		}
		
		timer.stopTimer();
		LogWrapper.d(this, "It took " + timer.getPassedTime() + " to execute and parse the request.");
		
		return result;
	}

	@Override
	public void cancel() {
		synchronized (mLock) {
			mState = State.CANCELLED;

			if (mState == State.RUNNING && mRequest != null) {
				try {
					mRequest.abort();
				} catch (UnsupportedOperationException e) {
				}
			}
		}
	}

	@Override
	public State getState() {
		return mState;
	}
	// TODO refactoring!
	private T doGet() {
		HttpClient httpClient = getHttpClient();
		mRequest = new HttpGet(URI.create(mUrl));
		
		LogWrapper.d(this, "Created request for " + mUrl);
		
		HttpResponse response;
		try {
			response = httpClient.execute(mRequest);
			ServerResponse serverResponse = getResponse(response);
			if (serverResponse != null) {
				synchronized (mLock) {
					mState = State.FINISHED;
				}
				
				LogWrapper.d(this, "Response is " + new String(serverResponse.getResponseBody()));

				return mResponseParser.parseResponse(serverResponse);
			}
		} catch (Exception exception) {
		}

		synchronized (mLock) {
			mState = State.FINISHED;
		}

		return null;
	}

	private T doPost() {
		// TODO
		return null;
	}

	private HttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		return httpClient;
	}

	private ServerResponse getResponse(HttpResponse response) throws IOException {
		if (response == null) {
			return null;
		}

		int responseCode = response.getStatusLine().getStatusCode();
		InputStream is = response.getEntity().getContent();

		ServerResponse serverResponse = new ServerResponse(responseCode, IOUtils.toByteArray(is));
		return serverResponse;
	}
}
