package com.hr.bankapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.bankapp.dto.DepositFormDto;
import com.hr.bankapp.dto.SaveFormDto;
import com.hr.bankapp.dto.TransferFormDto;
import com.hr.bankapp.dto.WithdrawFormDto;
import com.hr.bankapp.handler.exception.CustomRestfulException;
import com.hr.bankapp.repository.interfaces.AccountRepository;
import com.hr.bankapp.repository.interfaces.HistoryRepository;
import com.hr.bankapp.repository.model.Account;
import com.hr.bankapp.repository.model.History;

@Service
public class AccountService {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private HistoryRepository historyRepository;
	
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
	
	// 출금기능 로직 생각해보기
	// 1. 계좌 존재여부 확인
	// 2. 본인 계좌 여부 확인 -> select query
	// 3. 계좌 비번 확인
	// 4. 잔액 여부 확인 
	// 5. 출금 처리 -> update query
	// 6. 거래 내역 등록 -> inesrt query
	// 7. 트랜잭션 처리
	@SuppressWarnings("unused")
	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto withdrawFormDto, Integer principalId) {
		
		Account accountEntity = accountRepository.findByNumber(withdrawFormDto.getWAccountNumber());
		System.out.println(accountEntity.toString());
		// 1 
		if(accountEntity == null) {
			throw new CustomRestfulException("계좌가 존재하지 않습니다", HttpStatus.BAD_REQUEST);
		}
		// 2
		if(accountEntity.getUserId() != principalId) {
			throw new CustomRestfulException("본인 소유 계좌가 아닙니다", HttpStatus.BAD_REQUEST);
		}
		// 3
		if(accountEntity.getPassword().equals(withdrawFormDto.getWAccountPassword())){
			throw new CustomRestfulException("계좌 비밀번호가 틀렸습니다", HttpStatus.BAD_REQUEST);
		}
		// 4
		if(accountEntity.getBalance() < withdrawFormDto.getAmount()) {
			throw new CustomRestfulException("계좌 잔액이 부족합니다", HttpStatus.BAD_REQUEST);
		}
		// 5 (모델 객체 상태값 변경하기)
		// accountEntity.setBalance(accountEntity.getBalance() - withdrawFormDto.getAmount());
		accountEntity.withdraw(withdrawFormDto.getAmount());
		// 6 거래 내역 등록 
		/**
		 * insert into
		history_tb(
		amount, w_balance, d_balance,
		w_account_id, d_account_id)
		values(
		#{amount}, #{wBalance}, #{dBalance},
		#{wAccountId}, #{dAccountId}
		)
		 */
		History history = new History();
		history.setAmount(withdrawFormDto.getAmount());
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount != 1) {
			throw new CustomRestfulException("정상 처리되지않았습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	// 입금 처리 기능 
	// 트랜잭션 처리
	// 1. 계좌 존재 여부 확인
	// 2. 입금 처리 -> update
	// 3. 거래내역 등록 처리 -> insert
	
	public void updateAccountDeposit(DepositFormDto depositFormDto) {
		
		Account accountEntity = accountRepository.findByNumber(depositFormDto.getDAccountNumber());
		if(accountEntity == null) {
			throw new CustomRestfulException("해당 계좌가 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 객체 상태값 변경
		accountEntity.deposit(depositFormDto.getAmount());
		accountRepository.updateById(accountEntity);
		
		History history = new History();
		history.setAmount(depositFormDto.getAmount());
		history.setAmount(depositFormDto.getAmount());
		history.setWBalance(null);
		history.setDBalance(accountEntity.getBalance());
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());
		
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount != 1) {
			throw new CustomRestfulException("정상 처리되지않았습니다", HttpStatus.BAD_REQUEST);
		}		
	}
	// 이체 기능 만들기
	// 트랜잭션 처리
	// 1. 출금 계좌 존재 여부 확인 - select
	// 2. 입금 계좌 존재 여부 확인 - select
	// 3. 출금 계좌 본인 소유 확인 - 1(E) == (principal)
	// 4. 출금 계좌 비밀 번호 확인 - 1(E) == (Dto)
	// 5. 출금 계좌 잔액 여부 확인 - 1(E) == (Dto)
	// 6. 출금 계좌 잔액 변경 - update
	// 7. 입금 계좌 잔액 변경 - update
	// 8. 거래 내역 저장 - insert
	
	public void updateAccountTransfer(TransferFormDto transferFormDto, Integer principalId) {
		
		// 1
		Account withdrawAccountEntity = accountRepository.findByNumber(transferFormDto.getWAccountNumber());
		if(withdrawAccountEntity == null) {
			throw new CustomRestfulException("출금계좌가 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR );
		}
		// 2
		Account depositAccountEntity = accountRepository.findByNumber(transferFormDto.getDAccountNumber());
		if(depositAccountEntity == null) {
			throw new CustomRestfulException("입금계좌가 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 3
		withdrawAccountEntity.checkOwner(principalId);
		// 4 
		withdrawAccountEntity.checkPassword(transferFormDto.getWAccountPassword());
		// 5 
		withdrawAccountEntity.checkBalance(transferFormDto.getAmount());
		// 6 (출금 객체 상태값 변경 - 계좌 잔액 수정 처리)
		withdrawAccountEntity.withdraw(transferFormDto.getAmount());
		// 변경된 객체 상태값으로 DB update 처리
		accountRepository.updateById(withdrawAccountEntity);
		// 7 (입금 객체 상태 값 변경)
		depositAccountEntity.deposit(transferFormDto.getAmount());
		// 변경된 객체 상태값으로 DB update 처리 
		accountRepository.updateById(depositAccountEntity);
		// 8 
		History history = new History();
		history.setAmount(transferFormDto.getAmount());
		history.setWAccountId(withdrawAccountEntity.getId());
		history.setDAccountId(depositAccountEntity.getId());
		history.setWBalance(withdrawAccountEntity.getBalance());
		history.setDBalance(depositAccountEntity.getBalance());

		int resultRowCount = historyRepository.updateById(history);
		if(resultRowCount != 1) {
			throw new CustomRestfulException("정상처리 되지않았습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}
	
	
	

}
