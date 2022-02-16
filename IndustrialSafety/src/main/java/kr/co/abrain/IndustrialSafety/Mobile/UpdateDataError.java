/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Mobile;

import lombok.Data;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Data
public class UpdateDataError {
	private int code;
	private String msg;
	
	public UpdateDataError(int code, String id) {
		this.code = code;
		this.msg = id + UpdateDataErrorMsg.values()[code].ordinal();
	}
}

enum UpdateDataErrorMsg{
	ERR0(" invalid password or employee id");

	private final String getMessage;
	
	UpdateDataErrorMsg(String msg) {
		this.getMessage = msg;
	}
	
}