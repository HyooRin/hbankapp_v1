package com.hr.bankapp.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.hr.bankapp.dto.SaveFormDto;
import com.hr.bankapp.handler.exception.CustomRestfulException;
import com.hr.bankapp.handler.exception.unAuthorizedException;
import com.hr.bankapp.repository.model.Account;
import com.hr.bankapp.repository.model.User;
import com.hr.bankapp.service.AccountService;
import com.hr.bankapp.utils.Define;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private HttpSession session;
	@Autowired
	private AccountService accountService;

	/**
	 * 계좌목록 페이지	 * 
	 * @return 목록 페이지로 이동
	 */
	@GetMapping({ "/list", "/" })
	public String list(Model model) {

		// 유효성 -> 인증 검사 처리
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if(principal == null) {
			throw new unAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);			
		}
		List<Account> accountList = accountService.readAccountList(principal.getId());
		if(accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		}else {
			model.addAttribute("accountList", accountList);
		}
		// View 화면으로 데이터를 내려주는 기술
		// Model(권장)과 ModelAndView(동적으로 화면을 띄울때)
		return "/account/list";
	}

	// 출금페이지
	@GetMapping("/withdraw")
	public String withdraw() {

		return "/account/withdrawForm";
	}

	// 입금페이지
	@GetMapping("/deposit")
	public String deposit() {

		return "/account/depositForm";

	}

	// 이체 페이지
	@GetMapping("/transfer")
	public String transfer() {
		return "/account/transferForm";

	}

	// 계좌 생성페이지
	@GetMapping("/save")
	public String save() {
		// 인증 검사 처리
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if(user == null) {
			throw new unAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		return "/account/saveForm";
	}
	/**
	 * 계좌 생성
	 * 인증 검사
	 * 유효성 검사 처리 - 0원 입력가능 & 마이너스 입력 불가
	 * @param saveFormDto
	 * @return 계좌 목록 페이지
	 */
	@PostMapping("/save-proc")
	public String saveProc(SaveFormDto saveFormDto) {
		
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if(user == null) {
			throw new unAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		
		// 유효성 검사
		if(saveFormDto.getNumber() == null || saveFormDto.getNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if(saveFormDto.getPassword() == null || saveFormDto.getPassword().isEmpty()) {
			throw new CustomRestfulException("계좌 비밀번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if(saveFormDto.getBalance() == null || saveFormDto.getBalance() < 0) {
			throw new CustomRestfulException("잘못된 금액입니다.", HttpStatus.BAD_REQUEST);
		}
		// 서비스 호출
		accountService.createAccount(saveFormDto, user.getId());	
		
		return "redirect:/account/list";		
	}
	
	

	// 계좌상세 보기 페이지
	@GetMapping("/detail")
	public String detail() {
		return "";

	}

}
