package com.anddevbg.servercommunication;

/**
 * 
 * @author anddevbg@gmail.com
 * 
 */
public abstract class ResponseParser<T> {
	
	public abstract T parseResponse(ServerResponse response) throws Exception;
}
