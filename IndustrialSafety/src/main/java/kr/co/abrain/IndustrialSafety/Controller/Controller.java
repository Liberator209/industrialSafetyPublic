package kr.co.abrain.IndustrialSafety.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.abrain.IndustrialSafety.Mobile.ConfirmPassword;
import kr.co.abrain.IndustrialSafety.Mobile.Login;
import kr.co.abrain.IndustrialSafety.Mobile.MobileService;
import kr.co.abrain.IndustrialSafety.Mobile.PasswordRenewal;
import kr.co.abrain.IndustrialSafety.Mobile.Register;
import kr.co.abrain.IndustrialSafety.Mobile.UpdateData;
import kr.co.abrain.IndustrialSafety.Mobile.WatchData;
import kr.co.abrain.IndustrialSafety.Mobile.WatchService;
import kr.co.abrain.IndustrialSafety.Sensor.SensorParser;
import kr.co.abrain.IndustrialSafety.Sensor.SensorService;
import reactor.core.publisher.Mono;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@org.springframework.stereotype.Controller
public class Controller {
	private SensorService sensorService;
	private MobileService mobileService;
	private WatchService watchService;
	
	Controller(SensorService sensorService, MobileService mobileService, WatchService watchService){
		this.sensorService = sensorService;
		this.mobileService = mobileService;
		this.watchService = watchService;
	}
	
	@PostMapping("sensor")
	@ResponseBody
	public Mono<Object> SensorRequest(@RequestBody SensorParser requestedSensorData) {
		return this.sensorService.workerRecognition(requestedSensorData);
	}
	
	@PostMapping("register")
	@ResponseBody
	public Mono<Object> RegisterRequest(@RequestBody Register registerData){
		return this.mobileService.register(registerData);
	}
	
	@PostMapping("login")
	@ResponseBody
	public Mono<Object> LoginRequest(@RequestBody Login loginData){
		return this.mobileService.login(loginData);
	}
	
	@PostMapping("passwordRenewal")
	@ResponseBody
	public Mono<Object> PasswordRenewalRequest(@RequestBody PasswordRenewal passwordRenewal){
		return this.mobileService.passwordRenewal(passwordRenewal);
	}
	
	@PostMapping("confirmPassword")
	@ResponseBody
	public Mono<Object> ConfirmPasswordRequest(@RequestBody ConfirmPassword confirmPassword){
		return this.mobileService.confirmPassword(confirmPassword);
	}
	
	@PostMapping("updateData")
	@ResponseBody
	public Mono<Object> UpdateDataRequest(@RequestBody UpdateData updateData){
		return this.mobileService.updateData(updateData);
	}
	
	@ResponseBody
	@PostMapping("Dataapplewatch/upload")
	Mono<ResponseEntity<WatchData>> setWatchAPISensor(@RequestBody WatchData watch) {
		return this.watchService.saveWatchData(watch)
				.map(result -> {
					return ResponseEntity.ok(result);
				});
	}
	@ResponseBody
	@PostMapping("Dataapplewatch/confirm")
	Mono<List<WatchData>> getWatchAPISensor(@RequestBody String watch) {
		return this.watchService.getData(watch).collectList();
	}
}
