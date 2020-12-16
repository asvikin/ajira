package com.asvkin.ajira.exception;

public class DataParseException extends RuntimeException {
	public DataParseException(String message) {
		super(message);
	}
	
	public DataParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
