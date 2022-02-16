/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Sensor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import kr.co.abrain.IndustrialSafety.Employee.Employee;
import kr.co.abrain.IndustrialSafety.Equipment.Equipment;
import lombok.Data;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Data
@Document(collection="sensor")
public class Sensor {
	@Id
	private String id;
	private String type;
	private String deviceAddress;
	private String workerAddress;
	private String timeStamp;
	private String rssi;
	private Equipment device;
	private Employee worker;
}
