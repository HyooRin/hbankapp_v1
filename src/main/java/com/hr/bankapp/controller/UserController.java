package com.hr.bankapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hr.bankapp.dto.SignInFormDto;
import com.hr.bankapp.dto.SignUpFormDto;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@GetMapping("/sign-up")
	public String signUp() {
		
		return "/user/signUp";
	}
	
	/**
	 * 회원가입처리 
	 * @param signUpFormDto
	 * @return 리다이렉트 로그인페이지 
	 */
	@PostMapping("sign-up")
	public String signUpProc(SignUpFormDto signUpFormDto) {
		
		return "redirect:user/sign-in";
	}
	
	/**
	 * 로그인 폼
	 * @return 로그인 페이지
	 */
	@GetMapping("/sign-in")
	public String signIn() {
		
		return "user/signIn";		
	}
	
	/**
	 *  로그인 처리 
	 * @param signInFormDto
	 * @return 메인페이지 이동 (수정예정)
	 */
	@PostMapping("sign-in")
	public String signInProc(SignInFormDto signInFormDto) {
		
		// Todo 변경예정 
		return "/test/main";
		
	}
	
	
	
	
	

	
}
