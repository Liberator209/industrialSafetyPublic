/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Mobile;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
public interface WatchDataRepository extends ReactiveCrudRepository<WatchData, String> {
	Flux<WatchData> findByIdStartingWith(String userId);
}
