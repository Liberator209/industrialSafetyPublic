/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Sensor;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
public interface SensorRepository extends ReactiveCrudRepository<Sensor, String> {
	public Flux<Sensor> findByWorkerAddress(String workerAddress);
	public Flux<Sensor> findByDeviceAddress(String deviceAddress);
}
