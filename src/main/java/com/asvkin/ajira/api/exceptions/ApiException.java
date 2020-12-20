package com.asvkin.ajira.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@SuppressWarnings("unused")
public class ApiException extends RuntimeException {
	private final String message;
	private final HttpStatus httpStatus;
	private final Throwable cause;
	
	public ApiException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		this.cause = cause;
	}
	
	public ApiException(String message, HttpStatus httpStatus) {
		super(message);
		this.message = message;
		this.httpStatus = httpStatus;
		this.cause = null;
	}
	
	public ApiException(String message) {
		super(message);
		this.message = message;
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		this.cause = null;
	}
	
	public ApiException(String message, HttpStatus httpStatus, Throwable cause) {
		super(message, cause);
		this.message = message;
		this.httpStatus = httpStatus;
		this.cause = cause;
	}
	
	public ApiException(String message, String message1, HttpStatus httpStatus, Throwable cause) {
		super(message);
		this.message = message1;
		this.httpStatus = httpStatus;
		this.cause = cause;
	}
	
	public ApiException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.message = message;
		this.httpStatus = httpStatus;
		this.cause = cause;
	}
	
	public ApiException(Throwable cause, String message, HttpStatus httpStatus) {
		super(cause);
		this.message = message;
		this.httpStatus = httpStatus;
		this.cause = cause;
	}
	
	public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			HttpStatus httpStatus) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.message = message;
		this.httpStatus = httpStatus;
		this.cause = cause;
	}
}
