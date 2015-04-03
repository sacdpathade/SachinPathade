package com.denver.exception;

public class DenverValidationException extends DenverException {

	public DenverValidationException(String message, Throwable cause) {
		super("Validation Exception in Denver routing system " + message, cause);
	}

	public DenverValidationException(String message) {
		super("Validation Exception in Denver routing system " + message);
	}
}
