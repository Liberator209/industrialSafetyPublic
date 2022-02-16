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
public class PasswordRenewalError {
		private int code;
		private String msg;
		
		public PasswordRenewalError(int code, String id) {
			this.code = code;
			this.msg = id + RegisterErrorMsg.values()[code].ordinal();
		}
}

	enum PasswordRenewalErrorMsg{
		ERR0(" invalid fcm token");

		private final String getMessage;
		
		PasswordRenewalErrorMsg(String msg) {
			this.getMessage = msg;
		}
		
}
