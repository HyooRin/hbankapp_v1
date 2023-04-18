package com.hr.bankapp.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hr.bankapp.dto.SignUpFormDto;
import com.hr.bankapp.repository.model.User;

@Mapper
public interface UserRepository {
	
	public int insert(SignUpFormDto signUpFormDto);
	public int updateById(User user);
	public int deleteById(Integer id);
	public User findById(Integer id);
	public List<User> findAll();

}
