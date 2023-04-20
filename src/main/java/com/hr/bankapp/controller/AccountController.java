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

import com.hr.bankapp.dto.DepositFormDto;
import com.hr.bankapp.dto.SaveFormDto;
import com.hr.bankapp.dto.TransferFormDto;
import com.hr.bankapp.dto.WithdrawFormDto;
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
	 * 계좌목록 페이지
	 * 
	 * @return 목록 페이지로 이동
	 */
	@GetMapping({ "/list", "/" })
	public String list(Model model) {

		// 유효성 -> 인증 검사 처리
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new unAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		List<Account> accountList = accountService.readAccountList(principal.getId());
		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}
		// View 화면으로 데이터를 내려주는 기술
		// Model(권장)과 ModelAndView(동적으로 화면을 띄울때)
		return "/account/list";
	}

	// 출금페이지
	@GetMapping("/withdraw")
	public String withdraw() {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new unAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}

		return "/account/withdrawForm";
	}

	// 출금 처리 기능
	@PostMapping("/withdraw-proc")
	public String withdrawProc(WithdrawFormDto withdrawFormDto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new CustomRestfulException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		if (withdrawFormDto.getAmount() == null) {
			throw new CustomRestfulException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (withdrawFormDto.getAmount().longValue() <= 0) {
			throw new CustomRestfulException("출금액이 0원이하일 수는 없습니다.", HttpStatus.BAD_REQUEST);
		}
		if (withdrawFormDto.getWAccountNumber() == null || withdrawFormDto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if (withdrawFormDto.getWAccountPassword() == null || withdrawFormDto.getWAccountPassword().isEmpty()) {
			throw new CustomRestfulException("계좌비밀번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		accountService.updateAccountWithdraw(withdrawFormDto, principal.getId());
		return "redirect:/account/list";

	}

	// 입금페이지
	@GetMapping("/deposit")
	public String deposit() {

		if ((User) session.getAttribute(Define.PRINCIPAL) == null) {
			throw new unAuthorizedException("로그인 먼저해주세요", HttpStatus.UNAUTHORIZED);
		}
		return "/account/depositForm";
	}

	@PostMapping("/deposit-proc")
	public String depositProc(DepositFormDto depositFormDto) {

		if (depositFormDto.getAmount() == null) {
			throw new CustomRestfulException("금액 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if (depositFormDto.getAmount().longValue() <= 0) {
			throw new CustomRestfulException("입금금액이 0원 이하일 수 없습니다", HttpStatus.BAD_REQUEST);
		}
		if (depositFormDto.getDAccountNumber() == null || depositFormDto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		accountService.updateAccountDeposit(depositFormDto);
		return "redirect:/account/list";

	}

	// 이체 페이지
	@GetMapping("/transfer")
	public String transfer() {
		
		if(session.getAttribute(Define.PRINCIPAL) == null ) {
			throw new CustomRestfulException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}		
		return "/account/transferForm";
	}
	// 이체 기능 만들기
		@PostMapping("/transfer-proc")
		public String transferProc(TransferFormDto transferFormDto) {
			
			User principal = (User) session.getAttribute(Define.PRINCIPAL);
			if(principal == null) {
				throw new CustomRestfulException("로그인먼저해주세요", HttpStatus.UNAUTHORIZED);
			}
			
			// 1. 출금계좌번호 입력여부
			if(transferFormDto.getWAccountNumber() == null || transferFormDto.getWAccountNumber().isEmpty()) {
				throw new CustomRestfulException("출금계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
			}
			// 2. 입금계좌번호 입력 여부
			if(transferFormDto.getDAccountNumber() == null || transferFormDto.getDAccountNumber().isEmpty()) {
				throw new CustomRestfulException("입금계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
			}
			// 3. 출금 계좌 비밀번호 입력 여부 확인
			if(transferFormDto.getWAccountPassword() == null || transferFormDto.getWAccountPassword().isEmpty()) {
				throw new CustomRestfulException("출금계좌 비밀번호를 입력해주세요", HttpStatus.BAD_REQUEST);
			}
			// 4. 이체 금액 0원이상
			if(transferFormDto.getAmount() == null || transferFormDto.getAmount() <= 0) {
				throw new CustomRestfulException("이체금액이 0원 이하일 수 없습니다.", HttpStatus.BAD_REQUEST);
			}
			// 5. 출금계좌번호 임금계좌번호 동일 여부 확인 
			if(transferFormDto.getWAccountNumber().equals(transferFormDto.getDAccountNumber())) {
				throw new CustomRestfulException("출금계좌와 입금계좌는 같을 수 없습니다", HttpStatus.BAD_REQUEST);
			}
			// 서비스 호출
			accountService.updateAccountTransfer(transferFormDto, principal.getId());			
			return "redirect:/account/list";
		}

	// 계좌 생성페이지
	@GetMapping("/save")
	public String save() {
		// 인증 검사 처리
		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new unAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		return "/account/saveForm";
	}

	/**
	 * 계좌 생성 인증 검사 유효성 검사 처리 - 0원 입력가능 & 마이너스 입력 불가
	 * 
	 * @param saveFormDto
	 * @return 계좌 목록 페이지
	 */
	@PostMapping("/save-proc")
	public String saveProc(SaveFormDto saveFormDto) {

		User user = (User) session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new unAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}

		// 유효성 검사
		if (saveFormDto.getNumber() == null || saveFormDto.getNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if (saveFormDto.getPassword() == null || saveFormDto.getPassword().isEmpty()) {
			throw new CustomRestfulException("계좌 비밀번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if (saveFormDto.getBalance() == null || saveFormDto.getBalance() < 0) {
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
