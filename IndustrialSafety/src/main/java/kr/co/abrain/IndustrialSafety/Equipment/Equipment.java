/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Equipment;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import kr.co.abrain.IndustrialSafety.Employee.Employee;
import lombok.Data;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Data
@Document(collection="equipment")
public class Equipment {
	@Id
	private String id;
	private String name;
	private String deviceAddress;
	private Employee lastOccupant;
}
