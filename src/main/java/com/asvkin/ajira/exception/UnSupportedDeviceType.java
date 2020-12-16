package com.asvkin.ajira.exception;

public class UnSupportedDeviceType extends RuntimeException {
	public UnSupportedDeviceType(String message) {
		super(message);
	}
	
	public UnSupportedDeviceType(String message, Throwable cause) {
		super(message, cause);
	}
}
