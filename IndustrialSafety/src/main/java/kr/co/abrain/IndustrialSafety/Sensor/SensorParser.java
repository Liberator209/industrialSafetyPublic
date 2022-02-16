/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Sensor;

import java.util.List;

import lombok.Data;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Data
public class SensorParser {
	private String type;
	private String deviceAddress;
	private List<WorkerIdentify> workerIdentify;
	
	public WorkerIdentify findByWorkerAddress(String workerAddress) {
		for(WorkerIdentify worker : this.workerIdentify) {
			if(worker.getWorkerAddress().equals(workerAddress))
				return worker;
		}
		return new WorkerIdentify();
	}
}
