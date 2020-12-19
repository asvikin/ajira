package com.asvkin.ajira.exception;

public class UnSupportedDeviceTypeException extends RuntimeException {
	public UnSupportedDeviceTypeException(String message) {
		super(message);
		
	}
	
	public UnSupportedDeviceTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
