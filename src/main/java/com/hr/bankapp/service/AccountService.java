package com.hr.bankapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.bankapp.dto.SaveFormDto;
import com.hr.bankapp.handler.exception.CustomRestfulException;
import com.hr.bankapp.repository.interfaces.AccountRepository;
import com.hr.bankapp.repository.model.Account;

@Service
public class AccountService {
	@Autowired
	private AccountRepository accountRepository;
	
	// 서비스 로직 만들기 
	/**
	 * 계좌 생성 기능
	 * @param saveFormDto
	 * @param principalId
	 */
	public void createAccount(SaveFormDto saveFormDto, Integer principalId) {
		
		// saveFormDto를 변경 또는 신규생성
		// 1 단계 레벨
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(principalId);
		int resultRowCount = accountRepository.insert(account);
		if(resultRowCount != 1) {
			throw new CustomRestfulException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
			
		}		
	}
	
	// 계좌 목록 보기 기능
	@Transactional
	public List<Account> readAccountList(Integer userId){
		
		List<Account> list = accountRepository.findByUserId(userId);
		return list;		
	}
	
	

}
