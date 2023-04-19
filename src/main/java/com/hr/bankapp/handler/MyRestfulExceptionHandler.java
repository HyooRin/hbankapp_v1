package com.hr.bankapp.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hr.bankapp.handler.exception.CustomRestfulException;
/**
 * 예외시 
 * 데이터를 내려줄 수 있다
 */
@RestControllerAdvice // Ioc 기반 + AOP 기반 
public class MyRestfulExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public void exception(Exception e) {
	
		System.out.println(e.getClass().getName());
		System.out.println(e.getMessage());
	}
	
	// 사용자 정의 예외 클래스 활용
	@ExceptionHandler(CustomRestfulException.class)
	public String basicException(CustomRestfulException e) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("alert(' " + e.getMessage() + " ');");
		sb.append("history.back();");
		sb.append("</script>");		
		
		return sb.toString();
	}
	@ExceptionHandler(com.hr.bankapp.handler.exception.unAuthorizedException.class)
	public String unAuthorizedException(com.hr.bankapp.handler.exception.unAuthorizedException e) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("alert( '" + e.getMessage() + "' );");
		sb.append("location.href='/user/sign-in';");
		sb.append("</script>");	
		
		return sb.toString();		
	}
	
	

}
