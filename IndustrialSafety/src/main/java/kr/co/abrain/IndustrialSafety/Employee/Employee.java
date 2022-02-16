/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Employee;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Data
@Document(collection="employee")
public class Employee {
	@Id
	private String id;
	private String pw;
	private int work_type;
	private String name;
	private String work_equipment;
	private String email;
	private String gender;
	private String device;
	private boolean license;
	private String token;
}
