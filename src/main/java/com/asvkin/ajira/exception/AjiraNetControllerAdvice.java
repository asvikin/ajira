package com.asvkin.ajira.exception;

import com.asvkin.ajira.beans.ResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(value = "com.asvkin.ajira")
public class AjiraNetControllerAdvice {
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFoundException(final NotFoundException notFoundException) {
		return errorHandler(notFoundException, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidCommandException.class)
	public ResponseEntity<?> handleInvalidCommandException(final InvalidCommandException invalidCommandException) {
		return errorHandler(invalidCommandException, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException alreadyExistsException) {
		return errorHandler(alreadyExistsException, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataParseException.class)
	public ResponseEntity<?> handleDataParseException(DataParseException dataParseException) {
		return errorHandler(dataParseException, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidContentTypeException.class)
	public ResponseEntity<?> handleInvalidContentTypeException(InvalidContentTypeException invalidContentTypeException) {
		return errorHandler(invalidContentTypeException, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UnSupportedDeviceTypeException.class)
	public ResponseEntity<?> handleUnSupportedDeviceType(UnSupportedDeviceTypeException unSupportedDeviceTypeException) {
		return errorHandler(unSupportedDeviceTypeException, HttpStatus.BAD_REQUEST);
	}
	
	private ResponseEntity errorHandler(Exception exception, HttpStatus httpStatus) {
		return new ResponseEntity(new ResponseBean(exception.getMessage()), httpStatus);
	}
	
}
