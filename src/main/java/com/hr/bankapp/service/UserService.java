package com.hr.bankapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.bankapp.dto.SignInFormDto;
import com.hr.bankapp.dto.SignUpFormDto;
import com.hr.bankapp.handler.exception.CustomRestfulException;
import com.hr.bankapp.repository.interfaces.UserRepository;
import com.hr.bankapp.repository.model.User;

@Service // Ioc 대상
public class UserService {

	@Autowired // DI 처리 (객체 생성시 의존주입처리)
	private UserRepository userRepository;

	@Transactional
	// 메서드 호출이 시작될때 트랜잭션의 시작
	// 메서드 종료시 트랜젝션 종료 (commit)

	public void createUser(SignUpFormDto signUpFormDto) {

		int result = userRepository.insert(signUpFormDto);
		if (result != 1) {
			throw new CustomRestfulException("회원가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 로그인 서비스 처리
	 * @param signInFormDto
	 * @return userEntity 응답 
	 */
	public User signIn(SignInFormDto signInFormDto) {
		
		User userEntity = userRepository.findByUsernameAndPassword(signInFormDto);
		if(userEntity == null) {
			throw new CustomRestfulException("아이디 혹은 비밀번호가 잘 못 되었습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userEntity;		
	}
	
	

}
