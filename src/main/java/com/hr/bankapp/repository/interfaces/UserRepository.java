package com.hr.bankapp.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hr.bankapp.dto.SignInFormDto;
import com.hr.bankapp.dto.SignUpFormDto;
import com.hr.bankapp.repository.model.User;

@Mapper // MyBatis 의존 설정함(build.gradle 파일)
public interface UserRepository {
	// 코드 수정
	public int insert(SignUpFormDto signUpFormDto);
	public int updateById(User user);
	public int deleteById(Integer id);
	public User findById(Integer id);
	public List<User> findAll();
	
	// 추가 
	public User findByUsernameAndPassword(SignInFormDto signInFormDto);

}
