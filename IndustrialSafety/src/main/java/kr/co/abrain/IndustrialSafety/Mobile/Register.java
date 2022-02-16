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
public class Register {
	private String user_employee_id;
	private String user_name;
	private String user_email;
	private String user_pw;
	private int work_type;
	private String work_equipment;
	private String user_mac_addr;
}
