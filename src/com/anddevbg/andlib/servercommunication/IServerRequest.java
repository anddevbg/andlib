package com.anddevbg.andlib.servercommunication;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public interface IServerRequest {

	public String getId();

	public <T> T execute() throws ClientProtocolException, IOException;

	public void cancel();

	public State getState();

	public static enum State {
		/**
		 * Request is not executed yet.
		 */
		PENDING,
		/**
		 * Request is running.
		 */
		RUNNING,
		/**
		 * Request is executed.
		 */
		FINISHED,
		/**
		 * Request is cancelled.
		 */
		CANCELLED;
	}
}
