package com.hr.bankapp.dto;

import lombok.Data;

@Data
public class SaveFormDto {

	private String number;
	private String password;
	private Long balance;

}
