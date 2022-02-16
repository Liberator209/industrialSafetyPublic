/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Equipment;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
public interface EquipmentRepository extends ReactiveCrudRepository<Equipment, String> {
	Mono<Equipment> findByDeviceAddress(String deviceAddress);
}
