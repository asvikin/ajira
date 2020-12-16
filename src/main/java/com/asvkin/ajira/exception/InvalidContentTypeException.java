package com.asvkin.ajira.exception;

public class InvalidContentTypeException extends RuntimeException {
	public InvalidContentTypeException(String message) {
		super(message);
	}
	
	public InvalidContentTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
