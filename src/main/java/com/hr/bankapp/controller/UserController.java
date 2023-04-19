package com.hr.bankapp.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hr.bankapp.dto.SignInFormDto;
import com.hr.bankapp.dto.SignUpFormDto;
import com.hr.bankapp.handler.exception.CustomRestfulException;
import com.hr.bankapp.repository.model.User;
import com.hr.bankapp.service.UserService;
import com.hr.bankapp.utils.Define;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired // DI 처리
	private UserService userService;
	@Autowired
	private HttpSession session;

	@GetMapping("/sign-up")
	public String signUp() {

		return "/user/signUp";
	}

	// MIME TYPE : x-www-form-urlencoded
	// form : Query String 방식으로 들어온다
	// dto : object mapper 처리
	/**
	 * 회원가입처리
	 * 
	 * @param signUpFormDto
	 * @return 리다이렉트 로그인페이지
	 */

	@PostMapping("/sign-up")
	public String signUpProc(SignUpFormDto signUpFormDto) {

		// 1. 유효성 검사
		if (signUpFormDto.getUsername() == null || signUpFormDto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (signUpFormDto.getPassword() == null || signUpFormDto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력하세요", HttpStatus.BAD_REQUEST);

		}
		if (signUpFormDto.getFullname() == null || signUpFormDto.getFullname().isEmpty()) {
			throw new CustomRestfulException("fullname을 입력하세요", HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출
		userService.createUser(signUpFormDto);
		return "redirect:user/sign-in";
	}

	/**
	 * 로그인 폼
	 * 
	 * @return 로그인 페이지
	 */
	@GetMapping("/sign-in")
	public String signIn() {

		return "user/signIn";
	}

	/**
	 * 로그인 처리
	 * 
	 * @param signInFormDto
	 * @return 메인페이지 이동 (수정예정)
	 * 생각해보기 ! 
	 * GET 방식처리는 브라우저 히스토리에 남겨지기 때문에 예외로 로그인은 POST 방식처리(for 보안)
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto signInFormDto) {

		// 1. 유효성 검사 (인증검사가 더우선)
		if (signInFormDto.getUsername() == null || signInFormDto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (signInFormDto.getPassword() == null || signInFormDto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		// 서비스 호출
		// 세션: 사용자 정보 저장
		User principal = userService.signIn(signInFormDto);
		session.setAttribute(Define.PRINCIPAL, principal);

		return "/account/list";
	}
	
	@GetMapping("logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/sign-in";
	}

}
