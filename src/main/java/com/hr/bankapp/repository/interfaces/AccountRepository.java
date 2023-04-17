package com.hr.bankapp.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hr.bankapp.repository.model.Account;

@Mapper
public interface AccountRepository {
	
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(int id);	
	public List<Account> findAll();
	public Account findById(int id);

}