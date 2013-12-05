package com.anddevbg.andlib.servercommunication;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public class ServerResponse {
	
	private int mResponseCode;
	private byte[] mResponseBody;
	
	public ServerResponse(int responseCode, byte[] body) {
		mResponseCode = responseCode;
		mResponseBody = body;
	}
	
	public int getResponseCode() {
		return mResponseCode;
	}
	
	public byte[] getResponseBody() {
		return mResponseBody;
	}
}
