package com.hr.bankapp.handler.exception;

import org.springframework.http.HttpStatus;

public class unAuthorizedException extends RuntimeException {
	
	private HttpStatus status;
	
	public unAuthorizedException(String message, HttpStatus status) {
		
		super(message);
		this.status = status;
	}

}
