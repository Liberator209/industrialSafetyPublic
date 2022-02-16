/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Mobile;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Service
public class WatchService {
	private WatchDataRepository watchDataRepository;
	
	public WatchService(WatchDataRepository watchDataRepository) {
		this.watchDataRepository = watchDataRepository;
	}
	public Flux<WatchData> getData(String watchId){
		return this.watchDataRepository.findByIdStartingWith(watchId)
				.filter(ft -> ft.getUser_id().equals(watchId));
	}
	
	public Mono<WatchData> saveWatchData(WatchData watchData){
		watchData.setId(watchData.getUser_id()+"#"+watchData.getDatetime());
		return this.watchDataRepository.save(watchData);
	}
}
