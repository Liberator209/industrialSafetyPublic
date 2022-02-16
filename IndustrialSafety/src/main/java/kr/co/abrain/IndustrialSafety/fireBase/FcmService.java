/**
 * 
 */
package kr.co.abrain.IndustrialSafety.fireBase;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.abrain.IndustrialSafety.Employee.Employee;
import reactor.core.publisher.Mono;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
public class FcmService {

	FirebaseCloudMessageService fcm = new FirebaseCloudMessageService(new ObjectMapper());
	
	
	public Mono<String> sendMessage(Employee employee, String title, String data) throws IOException{
		System.out.println(title + data);
			fcm.sendMessageTo(employee.getToken(), title , data);
			return Mono.just(employee.getToken());
	
			
	}
	
	
}
