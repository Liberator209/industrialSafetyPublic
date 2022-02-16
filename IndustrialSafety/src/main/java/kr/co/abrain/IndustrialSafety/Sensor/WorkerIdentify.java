/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Sensor;

import lombok.Data;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Data
public class WorkerIdentify {
	private String workerAddress;
	private String timeStamp;
	private String rssi;
}
